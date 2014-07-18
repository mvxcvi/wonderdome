(ns org.playasophy.wonderdome.mode.lantern
  (:require
    [org.playasophy.wonderdome.mode.core :as mode]
    [org.playasophy.wonderdome.util.color :as color]))


(def ^:private ^:const adjustment-rate
  "Rate at which the brightness level changes per millisecond."
  0.0005)


(defrecord LanternMode
  [brightness]

  mode/Mode

  (update
    [this event]
    (condp = [(:type event) (:button event)]
      [:gamepad/press :L]
      (assoc this :brightness 0.0)

      [:gamepad/press :R]
      (assoc this :brightness 1.0)

      [:gamepad/repeat :y-axis]
      (let [delta (* (or (:value event) 0)
                     (or (:elapsed event) 0)
                     adjustment-rate)
            level (-> brightness (+ delta) (min 1.0) (max 0.0))]
        (assoc this :brightness level))

      this))


  (render
    [this pixel]
    (color/gray brightness)))


(defn lantern
  "Creates a new lantern mode with starting brightness."
  [brightness]
  (LanternMode. brightness))
