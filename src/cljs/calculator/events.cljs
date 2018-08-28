(ns calculator.events
  (:require
    [re-frame.core :as rf]
    ))

(def default-db
  {:state ::ready
   :op-stack []
   :working-num 0})

(def state-machine
  {::ready {::entered-num ::entering-nums
            ::equal ::ready}
   ::entering-nums {::operation ::operation ;wtf
                    ::entered-num ::entering-nums
                    ::equal ::equals}
   ::operation {::entered-num ::entering-nums
                ::operation ::operation ;;todo: this is wrong, it'll calculate if you switch operations
                ::equal ::equals}
   ::equals {::equal ::equals
             ::operation ::ready}})

;states: ready, entering-nums, operation, equals,

;;todo need to add many more states to make functionality more granular
;todo Do not store state in DB, store in context
;todo as a Rule the event handlers shouldn't know about state

(defn next-state
  [fsm current-state transition]
  (get-in fsm [current-state transition]))

(defn update-next-state
  [db event]
  (if-let [new-state (next-state state-machine (:state db) event)]
    (assoc db :state new-state)
    db))

(def state-machine-intercept
  (rf/->interceptor
    :id :state-machine-intercept
    :before (fn [{{:keys [db event]} :coeffects :as context}]
              (if (next-state state-machine (:state db) (first event))
                context
                (update context :queue butlast)))
    :after (fn [context]
             (when (get-in context [:effects :db])
               (update-in context [:effects :db]
                          #(update-next-state % (get-in context [:coeffects :event 0])))))))

(rf/reg-event-db
  ::initialize-db
  (fn [_ _]
    default-db))

(rf/reg-event-db
  ::entered-num
  [rf/debug state-machine-intercept]
  (fn [db [_ num]]
    (if (= ::entering-nums (:state db))
      (update db :working-num #(-> % (* 10) (+ num)))
      (assoc db :working-num num))))

(rf/reg-event-db
  ::clear-display
  (fn [db _]
    default-db))

(rf/reg-event-db
  ::delete
  (fn [db _]
    (update db :working-num #(quot % 10))))

(rf/reg-event-db
  ::operation
  [rf/debug state-machine-intercept]
  (fn [{:keys [op-stack working-num state] :as db} [_ op-fn]]
    (cond
      (fn? (peek op-stack))
      (assoc db :op-stack (vector ((peek op-stack) (peek (pop op-stack)) working-num) op-fn))

      (not-empty op-stack)
      (update db :op-stack conj op-fn)

      (= ::entering-nums state)
      (assoc db :op-stack [working-num op-fn])

      :else
      db)))

(rf/reg-event-db
  ::equal
  [rf/debug state-machine-intercept]
  (fn [{:keys [op-stack working-num state] :as db} _]
    (cond
      (= ::equals state)
      (let [new-num ((peek op-stack) (peek (pop op-stack)) working-num)]
        (assoc db :working-num new-num))

      (fn? (peek op-stack))
      (let [new-num ((peek op-stack) (peek (pop op-stack)) working-num)]
        (assoc db :op-stack (vector working-num (peek op-stack))
                  :working-num new-num))

      :else
      db)))
