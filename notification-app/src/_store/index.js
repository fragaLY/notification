import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

const store = new Vuex.Store({
    state: {
        theme: false,
        event: {
            key: '',
            notification: {
                id: '',
                sender: '',
                receiver: ''
            }
        },
        events: [],
        messages: []
    },
    getters: {
        theme: state => {
            return state.theme
        },
        events: state => {
            return state.events
        },
        messages: state => {
            return state.messages
        }
    },
    mutations: {
        UPDATE_THEME(state, enable) {
            state.dark = enable
        },
        ADD_EVENT(state, event) {
            state.events.push(event)
        },
        UPDATE_EVENT(state, event) {
            state.events = state.events.map(e => {
                if (e.key === event.key && e.notification.id === event.notification.id) {
                    let result = {...e, ...event};
                    result.notification = event.notification;
                    return result;
                }
                return e;
            })
        },
        UPDATE_MESSAGES(state, messages) {
            state.messages = messages
        }
    },
    actions: {
        theme({commit}, dark) {
            commit('UPDATE_THEME', dark)
        },
        addEvent({commit}, event) {
            commit('ADD_EVENT', event)
        },
        updateEvent({commit}, event) {
            commit('UPDATE_EVENT', event)
        },
        updateMessages({commit}, messages) {
            commit('UPDATE_MESSAGES', messages)
        }
    }
});

export default store;
