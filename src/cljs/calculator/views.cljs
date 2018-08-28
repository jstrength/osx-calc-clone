(ns calculator.views
  (:require
    [re-frame.core :as rf]
    [calculator.events :as events]
    [calculator.subs :as subs]
    [re-frame.subs :as rf-subs]))

(def <subs (comp deref rf-subs/subscribe))

(def num-to-str {0 "zero"
                 1 "one"
                 2 "two"
                 3 "three"
                 4 "four"
                 5 "five"
                 6 "six"
                 7 "seven"
                 8 "eight"
                 9 "nine"})

(defn create-num-button [num]
  [:button.btn.btn-outline-dark.col-2 {:on-click #(rf/dispatch [::events/entered-num num])
                                       :id (num-to-str num)}
   num])

(defn main-panel []
  [:div.container
   [:div {:style {:height "50px"}} @re-frame.db/app-db]
   [:h1.text-center "Basic Calculator"]
   [:div.simple-calc {:style {:min-width "200px"}}
    [:div.row [:h1.col-8.text-right (<subs [::subs/display])]]
    [:div.row
     [:button#clr.btn.btn-outline-dark.col-2
      {:on-click #(rf/dispatch [::events/clear-display])}
      "C"]
     [:button#negate.btn.btn-outline-dark.col-2  "+/-"]
     [:button#percent.btn.btn-outline-dark.col-2  "%"]
     [:button#multiply.btn.btn-outline-dark.col-2 {:on-click #(rf/dispatch [::events/operation *])} "*"]]
    [:div.row [create-num-button 7] [create-num-button 8] [create-num-button 9]
     [:button#divide.btn.btn-outline-dark.col-2 {:on-click #(rf/dispatch [::events/operation /])} "/"]]
    [:div.row [create-num-button 4] [create-num-button 5] [create-num-button 6]
     [:button#subtract.btn.btn-outline-dark.col-2 {:on-click #(rf/dispatch [::events/operation -])} "-"]]
    [:div.row [create-num-button 1] [create-num-button 2] [create-num-button 3]
     [:button#add.btn.btn-outline-dark.col-2 {:on-click #(rf/dispatch [::events/operation +])} "+"]]
    [:div.row
     [:button#zero.btn.btn-outline-dark.col-4 {:on-click #(rf/dispatch [::events/entered-num 0])} 0]
     [:button#decimal.btn.btn-outline-dark.col-2 "."]
     [:button#equal.btn.btn-outline-dark.col-2 {:on-click #(rf/dispatch [::events/equal])} "="]]]])
