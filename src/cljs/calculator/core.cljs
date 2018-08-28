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

(defn handle-keypress [element]
  (doto element
    (animate-btn)
    (.click)))

(defn setup []
  (aset js/document "onkeydown"
        (fn [e] (case (.-which e)
                  48 (handle-keypress (.getElementById js/document "zero"))
                  49 (handle-keypress (.getElementById js/document "one"))
                  50 (handle-keypress (.getElementById js/document "two"))
                  51 (handle-keypress (.getElementById js/document "three"))
                  52 (handle-keypress (.getElementById js/document "four"))
                  53 (handle-keypress (.getElementById js/document "five"))
                  54 (handle-keypress (.getElementById js/document "six"))
                  55 (handle-keypress (.getElementById js/document "seven"))
                  56 (handle-keypress (.getElementById js/document (if (.-shiftKey e) "multiply" "eight")))
                  57 (handle-keypress (.getElementById js/document "nine"))

                  8 (re-frame/dispatch [::events/delete])
                  13 (handle-keypress (.getElementById js/document "equal"))
                  27 (handle-keypress (.getElementById js/document "clr"))
                  187 (handle-keypress (.getElementById js/document (if (.-shiftKey e) "add" "equal")))
                  189 (handle-keypress (.getElementById js/document "subtract"))
                  190 (handle-keypress (.getElementById js/document "decimal"))
                  191 (handle-keypress (.getElementById js/document "divide"))
                  nil))))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")
                  setup))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
