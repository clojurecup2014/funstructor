(ns funstructor.game-state
  (:require
   [clojure.set :as set]))

(def global-state (atom {:pending #{}
                         :uuid-channel-map {}
                         :channel-uuid-map {}
                         :games {}}))

;; (defn init-game-state []
;;   (funstructor.commands/pending-checker))

(defn current-global-state []
  @global-state)

(defn add-channel [global-state channel uuid]
  (-> global-state
      (update-in [:uuid-channel-map] assoc uuid channel)
      (update-in [:channel-uuid-map] assoc channel uuid)))

(defn add-pending [global-state uuid]
  (update-in global-state [:pending] conj uuid))

(defn pending-players [global-state]
  (:pending global-state))

(defn channel-for-uuid [global-state uuid]
  (get-in global-state [:uuid-channel-map uuid]))

(defn get-pending-pair [global-state]
  (let [pending (pending-players global-state)]
    (when (>= 2 (count pending))
      [(first pending-players) (second pending-players)])))

(defn remove-from-pending [global-state & uuids]
  (update-in global-state [:pending] set/difference uuids))

(defn update-global-state [new-state]
  (swap! global-state (fn [_] new-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GAME LOGIC
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn gap []
  {:terminal :gap
   :value nil})

(defn make-player-state [opponent]
  {:opponent opponent
   :cards []
   :funstruct [(gap)]})

(defn make-game [p1 p2]
  {:players
   {p1 (make-player-state p2)
    p2 (make-player-state p1)}
   :current-turn p1})

(defn get-opponent-uuid [game uuid]
  (get-in game [:players uuid :opponent]))
