(ns calculator.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [calculator.events :as events]
    [calculator.views :as views]
    [calculator.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn animate-btn [element]
  (.add (.-classList element) "active")
  (js/setTimeout (fn [] (.remove (.-classList element) "active")) 150))

(defn handle-keypress [element-name]
  (doto (.getElementById js/document element-name)
    (animate-btn)
    (.click)))

(defn set-keybindings []
  (aset js/document "onkeydown"
        (fn [e]
          (case (.-which e)
            48 (handle-keypress "zero")
            49 (handle-keypress "one")
            50 (handle-keypress "two")
            51 (handle-keypress "three")
            52 (handle-keypress "four")
            53 (handle-keypress (if (.-shiftKey e) "percent" "five"))
            54 (handle-keypress "six")
            55 (handle-keypress "seven")
            56 (handle-keypress (if (.-shiftKey e) "multiply" "eight"))
            57 (handle-keypress "nine")

            8 (re-frame/dispatch [::events/delete])
            12 (handle-keypress "clr")
            27 (handle-keypress "clr")
            106 (handle-keypress "multiply")
            107 (handle-keypress "add")
            13 (handle-keypress "equal")
            187 (handle-keypress (if (.-shiftKey e) "add" "equal"))
            189 (handle-keypress (if (.-altKey e) "negate" "subtract"))
            109 (handle-keypress (if (.-altKey e) "negate" "subtract"))
            110 (handle-keypress "decimal")
            190 (handle-keypress "decimal")
            111 (handle-keypress "divide")
            191 (handle-keypress "divide")
            nil))))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")
                  set-keybindings))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
