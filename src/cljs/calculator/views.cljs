(ns calculator.views
  (:require
    [re-frame.core :as re-frame]
    [calculator.events :as events]
    [calculator.subs :as subs]
    [re-frame.subs :as rf-subs]))

(def <subs (comp deref rf-subs/subscribe))

(defn create-num-button [num]
  [:button {:on-click #(re-frame/dispatch [::events/update-display num])}
   num])

(defn main-panel []
  [:div
   [:h1 "Jacobs Calculator"]
   [:div.calculator
    (str @re-frame.db/app-db)
    [:div.display [:h1 (<subs [::subs/display])]]
    [:div [create-num-button 7] [create-num-button 8] [create-num-button 9]]
    [:div [create-num-button 4] [create-num-button 5] [create-num-button 6]]
    [:div [create-num-button 1] [create-num-button 2] [create-num-button 3]]
    [:div [create-num-button 0]]
    [:button {:on-click #(re-frame/dispatch [::events/clear-display])} "Clear"]
    [:div
     [:button {:on-click #(re-frame/dispatch [::events/operation +])} "+"]
     [:button {:on-click #(re-frame/dispatch [::events/operation -])} "-"]
     [:button {:on-click #(re-frame/dispatch [::events/operation *])} "*"]
     [:button {:on-click #(re-frame/dispatch [::events/operation /])} "/"]
     [:button {:on-click #(re-frame/dispatch [::events/equal])} "="]]
    ]
   ])
