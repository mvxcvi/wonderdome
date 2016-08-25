(ns playasophy.wonderdome.mode.tunes
  (:require
    [playasophy.wonderdome.geometry.sphere :as sphere :refer [pi tau]]
    [playasophy.wonderdome.mode.core :as mode]
    [playasophy.wonderdome.util.color :as color]
    [playasophy.wonderdome.util.control :as ctl]))


;; General idea:
;; - Divide up the circle into `n` slices, each assigned to a part of the
;; frequency spectrum.
;; - Assign a rainbow spectrum to each of the slices.
;; - As time passes, rotate the angular offset of slices in physical space.
;; - As time passes, rotate the offset into the rainbow for each slice.
;; - Maintain a rolling average of the power in a particular frequency. Power
;; decay is controlled by `falloff` parameter.
;; - Adjust the gain per segment based on recently observed history to try to
;; keep values normalized.
;;
;; For a given slice, pixels along the slice will be colored based on the
;; rainbow offset. The current power in the slice determines how far the color
;; should extend away from the polar axis. More energy = further coloring.
;;
;; To color a particular pixel, figure out which two slices it falls between,
;; average the color and intensity. Should be a quick lookup ideally.
;;
;; As a stretch goal, if beats are detected, color the first few pixels of the
;; strips (up to a max) a totally different color which fades back to zero.


(defn- azimuth->bands
  "Given an azimuthal angle (around the pole), determine the two bands the
  angle falls between. Returns a vector containing the index of the first and
  second bands and the proportion between the two."
  [n angle]
  {:pre [(pos? n)]}
  (let [band-width (/ tau n)
        angle' (if (neg? angle)
                 (+ angle tau)
                 angle)
        angle-div (/ angle' band-width)
        low-band (int (Math/floor angle-div))
        high-band (int (Math/ceil angle-div))]
    [low-band
     (if (<= n high-band) 0 high-band)
     (- angle-div low-band)]))



(defrecord TunesMode
  [bands          ; Average energy per band
   gain           ; Gain per band (static for now)
   falloff        ; How quickly energy levels decay (frac/ms)
   smoothing      ; Proportion to smooth input samples by (fraction of old average to keep)
   rotation       ; How much to rotate each band spatially
   rotation-rate  ; How fast the rotation changes over time
   color-shift    ; How much to shift the colors
   shift-rate     ; How fast the color shifts over time
   log-next?      ; Debugging helper state
   ]

  mode/Mode

  (update
    [this event]
    (case [(:type event) (:input event)]
      [:time/tick nil]
      (let [elapsed (or (:elapsed event) 0.0)]
        (cond->
          (assoc this
               :bands (mapv (fn [p] (* p (ctl/bound [0.0 1.0] (- 1.0 (* falloff elapsed))))) bands)
               :rotation (sphere/wrap-angle (+ rotation (* elapsed (/ rotation-rate 1000))))
               :color-shift (ctl/wrap [0.0 1.0] (+ color-shift (* elapsed (/ shift-rate 1000)))))
          (compare-and-set! log-next? true false)
            (doto prn)))

      [:audio/beat nil]
      ; TODO: something
      this

      [:audio/freq nil]
      (assoc this
             :bands (mapv
                      (fn [avg band-gain energy]
                        (+ (* smoothing (or avg 0.0))
                           (* (- 1 smoothing)
                              (or band-gain 0.0)
                              (or energy 0.0))))
                      bands gain (:spectrum event)))

      [:button/press :A]
      (do (reset! log-next? true)
          this)

      ; TODO: add controls for adjusting gain, falloff, rotation-rate, shift-rate

      ; default
      this))


  (render
    [this pixel]
    (let [[_ polar azimuth] (:sphere pixel)
          [i1 i2 p] (azimuth->bands (count bands) (+ azimuth rotation))
          hue-index (/ (+ i1 p) (count bands))
          energy (+ (* (nth bands i1) (- 1 p))
                    (* (nth bands i2) p))
          polar-max (* 1/6 tau)
          transition-point (* polar-max (- 1 (Math/exp (- energy))))
          transition-width (/ tau 16)
          brightness (cond
                       (< polar (- transition-point transition-width))
                         1.0
                       (> polar (+ transition-point transition-width))
                         0.0
                       :else
                         (- 1.0 (/ (- polar (- transition-point transition-width))
                                   transition-width)))]
      (color/hsv
        (ctl/wrap [0.0 1.0] (+ hue-index color-shift))
        1
        brightness))))


(defn init
  "Creates a new sound-reactive mode with `n` frequency bands."
  [n & {:as opts}]
  (map->TunesMode
    (merge
      {:gain (vec (take n (repeat 1.0)))
       :falloff 0.005
       :smoothing 0.3
       :rotation-rate 0.5
       :shift-rate 0.0}
      opts
      {:bands (vec (take n (repeat 0.0)))
       :rotation 0.0
       :color-shift 0.0
       :log-next? (atom false)})))
