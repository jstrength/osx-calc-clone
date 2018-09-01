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

(def button-classes ["btn" "btn-outline-dark" "col-2"])

(defn create-num-button [num]
  [:button {:class button-classes
            :on-click #(rf/dispatch [::events/entered-num num])
            :id (num-to-str num)}
   num])

(defn main-panel []
  [:div.container
   [:div {:style {:height "50px"}} @re-frame.db/app-db]
   [:h1.text-center "Basic Calculator"]
   [:div.simple-calc {:style {:min-width "200px"}}
    [:div.row [:h1.col-8.text-right (<subs [::subs/display])]]
    [:div.row
     [:button#clr {:class button-classes
                   :on-click #(rf/dispatch [::events/clear-display])}
      "C"]
     [:button#negate {:class button-classes}  "+/-"]
     [:button#percent {:class button-classes}  "%"]
     [:button#multiply {:class button-classes :on-click #(rf/dispatch [::events/operation *])} "*"]]
    [:div.row [create-num-button 7] [create-num-button 8] [create-num-button 9]
     [:button#divide {:class button-classes :on-click #(rf/dispatch [::events/operation /])} "/"]]
    [:div.row [create-num-button 4] [create-num-button 5] [create-num-button 6]
     [:button#subtract {:class button-classes :on-click #(rf/dispatch [::events/operation -])} "-"]]
    [:div.row [create-num-button 1] [create-num-button 2] [create-num-button 3]
     [:button#add {:class button-classes :on-click #(rf/dispatch [::events/operation +])} "+"]]
    [:div.row
     [:button#zero {:class ["btn" "btn-outline-dark" "col-4"] :on-click #(rf/dispatch [::events/entered-num 0])} 0]
     [:button#decimal {:class button-classes} "."]
     [:button#equal {:class button-classes :on-click #(rf/dispatch [::events/equal])} "="]]]])
