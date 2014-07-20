(ns org.playasophy.wonderdome.input.middleware
  "Functions for providing system capabilities by handling input events."
  (:require
    [org.playasophy.wonderdome.state :as state]))

; Handler functions recieve the current state of the system and an input event
; and return the updated system state. Middleware wraps a handler function to
; produce a new handler with some extra logic. This is very similar to Ring
; middlewares.


(defn print-events
  "Debugging middleware which prints out events as they pass through the
  handler stack. A predicate function may be provided to filter the events
  shown."
  ([handler]
   (print-events handler (constantly true)))
  ([handler pred]
   (fn [state event]
     (when (pred event)
       (prn event))
     (handler state event))))


(defn mode-selector
  "Uses :select button presses to change the current mode."
  ([handler]
   (fn [state event]
     (if (= [:button/press :select] [(:type event) (:button event)])
       (state/next-mode state)
       (handler state event)))))


(defn autocycle-modes
  "Adds properties to the event state and automatically switches the current
  mode if no input matching a predicate has been received in a certain amount
  of time."
  [handler input?]
  (fn [state event]
    (let [period (or (:autocycle/period state) 300)
          now (System/currentTimeMillis)
          next-cycle (+ now (* 1000 period))]
      (cond-> state
        ; Default autocycling to enabled.
        (nil? (find state :autocycle/enabled))
        (assoc :autocycle/enabled true)

        ; Default period to 5 minutes.
        (nil? (:autocycle/period state))
        (assoc :autocycle/period period)

        ; If no target time is set, add it.
        (nil? (:autocycle/at state))
        (assoc :autocycle/at next-cycle)

        ; Input events push autocycle target back.
        (input? event)
        (assoc :autocycle/at next-cycle)

        ; If target cycle time is passed, rotate modes.
        (and (:autocycle/enabled state)
             (:autocycle/at state)
             (> now (:autocycle/at state)))
        (-> (assoc :autocycle/at next-cycle)
            state/next-mode)

        ; Forward events to handler chain.
        true
        (handler event)))))


; Konami code middleware:
; Watches button inputs, keeping track of the sequence of input events; if the
; last input was too far in the past, clears the buffer. If the sequence matches
; the code, changes the current mode to the easter-egg mode. Probably lets the
; autocycle or manual mode change handle switching out of it.
