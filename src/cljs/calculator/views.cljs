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
  [:button.calcBtn
   {:on-click #(rf/dispatch [::events/number num])
    :id (num-to-str num)}
   num])

(defn main-panel []
  [:div.container
   ;[:div {:style {:height "50px"}} @re-frame.db/app-db]
   [:h1 "Basic Calculator"]
   [:div.simpleCalc
    [:div.display
     [:div.displayText (<subs [::subs/display])]]
    [:div
     [:button#clr.calcBtn {:on-click #(rf/dispatch [::events/clear-display])} "AC"]
     [:button#negate.calcBtn { :on-click #(rf/dispatch [::events/negate])} "±"]
     [:button#percent.calcBtn { :on-click #(rf/dispatch [::events/percent])} "%"]
     [:button#divide.calcBtn.orange { :on-click #(rf/dispatch [::events/operation /])} "÷"]]
    [:div [create-num-button 7] [create-num-button 8] [create-num-button 9]
     [:button#multiply.calcBtn.orange {:on-click #(rf/dispatch [::events/operation *])} "×"]]
    [:div [create-num-button 4] [create-num-button 5] [create-num-button 6]
     [:button#subtract.calcBtn.orange { :on-click #(rf/dispatch [::events/operation -])} "-"]]
    [:div [create-num-button 1] [create-num-button 2] [create-num-button 3]
     [:button#add.calcBtn.orange { :on-click #(rf/dispatch [::events/operation +])} "+"]]
    [:div
     [:button#zero.longCalcBtn {:style {:border-bottom-left-radius "5px"} :on-click #(rf/dispatch [::events/number 0])} 0]
     [:button#decimal.calcBtn { :on-click #(rf/dispatch [::events/decimal])} "."]
     [:button#equal.calcBtn.orange {:style {:border-bottom-right-radius "5px"} :on-click #(rf/dispatch [::events/equal])} "="]]]])
