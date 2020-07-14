import axios from "axios";
import store from "../_store";

const instance = axios.create({
    withCredentials: false,
    timeout: 10000,
    baseURL: "http://localhost:8083" //todo vk: move to gateway port
});

instance.interceptors.response.use(response => {
    return response;
}, error => {
    if (error.response.status !== 204) {
        store.dispatch('updateMessages', [error]).then();
    }
    return Promise.reject(error);
});

instance.interceptors.request.use(request => {
    return request
}, error => Promise.reject(error));

export default instance;