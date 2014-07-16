(ns org.playasophy.wonderdome.util.color-test
  (:require
    [clojure.test :refer :all]
    [org.playasophy.wonderdome.util.color :as color]
    [quil.core :as quil]))


;;;;; COLOR HARNESS ;;;;;

(defn- setup-harness
  []
  (quil/text-font (quil/create-font "Courier" 18 true))
  (quil/background 0)
  (quil/stroke 0))


(defn- render-harness
  []
  ; red, green, and blue blocks
  (quil/with-translation [20 20]
    (quil/fill (color/rgb 0.75))
    (quil/text "primary colors" 0 0)
    (quil/fill (color/rgb 1 0 0))
    (quil/rect 0 10 40 40)
    (quil/fill (color/rgb 0 1 0))
    (quil/rect 50 10 40 40)
    (quil/fill (color/rgb 0 0 1))
    (quil/rect 100 10 40 40))

  ; RGB blending
  (quil/with-translation [20 90]
    (quil/fill (color/rgb 0.75))
    (quil/text "RGB blending" 0 0)
    (quil/fill (color/blend-rgb 0.5 (color/rgb 1 0 0) (color/rgb 0 1 0)))
    (quil/rect 0 10 40 40)
    (quil/fill (color/blend-rgb 0.5 (color/rgb 0 1 0) (color/rgb 0 0 1)))
    (quil/rect 50 10 40 40)
    (quil/fill (color/blend-rgb 0.5 (color/rgb 0 0 1) (color/rgb 1 0 0)))
    (quil/rect 100 10 40 40))

  ; HSV blending
  (quil/with-translation [20 160]
    (quil/fill (color/rgb 0.75))
    (quil/text "HSV blending" 0 0)
    (quil/fill (color/blend-hsv 0.5 (color/rgb 1 0 0) (color/rgb 0 1 0)))
    (quil/rect 0 10 40 40)
    (quil/fill (color/blend-hsv 0.5 (color/rgb 0 1 0) (color/rgb 0 0 1)))
    (quil/rect 50 10 40 40)
    (quil/fill (color/blend-hsv 0.5 (color/rgb 0 0 1) (color/rgb 1 0 0)))
    (quil/rect 100 10 40 40))
  )


(defn color-harness
  "Creates and starts a sketch to demonstrate the color utility functions."
  []
  (let []
    (quil/sketch
      :title "Wonderdome Color Harness"
      :setup setup-harness
      :draw render-harness
      :size [800 600])))
