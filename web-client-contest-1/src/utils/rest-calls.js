import {CONTEST_TASKS_BASE_URL} from "./consts.js";

function status(response) {
    console.log("Response status " + response.status);
    if (response.status >= 200 && response.status < 300) return Promise.resolve(response);
    else return Promise.reject(new Error(response.statusText));
}

export function GetTasks() {
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    let antet = {
        method: 'GET',
        headers: headers,
        mode: 'cors'
    };
    let request = new Request(CONTEST_TASKS_BASE_URL, antet);
    console.log('Before the GET request');

    return fetch(request)
        .then(status)
        .then(response => response.json())
        .then(data => {
            console.log('Data received ', data);
            return data;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function DeleteTask(id) {
    console.log('aista ii id u', id);
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    let antet = {
        method: 'DELETE',
        headers: headers,
        mode: 'cors'
    };

    const deleteURL = CONTEST_TASKS_BASE_URL + '/' + id;
    console.log("Delete URL " + deleteURL);
    return fetch(deleteURL, antet)
        .then(status)
        .then(response => {
            console.log('Received response ' + response.status);
            return response.text();
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function AddTask(task) {
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');
    let antet = {
        method: 'POST',
        headers: headers,
        mode: 'cors',
        body: JSON.stringify(task)
    };

    return fetch(CONTEST_TASKS_BASE_URL, antet)
        .then(status)
        .then(response => {
            console.log('Received response ' + response.status);
            return response.text();
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function ModifyTask(id, task){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');
    let antet = {
        method: 'PUT',
        headers: headers,
        mode: 'cors',
        body: JSON.stringify(task)
    };

    const modifyURL = CONTEST_TASKS_BASE_URL + '/' + id;
    console.log("Modify URL " + modifyURL);
    return fetch(modifyURL, antet)
        .then(status)
        .then(response => {
            console.log('Received response ' + response.status);
            return response.text();
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function GetAgeGroup(id){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    let antet = {
        method: 'GET',
        headers: headers,
        mode: 'cors'
    }

    const ageURL = CONTEST_TASKS_BASE_URL + '/' + id;
    return fetch(ageURL, antet)
        .then(status)
        .then(response => response.json())
        .then(data => {
            console.log('Data received ', data.ageGroup);
            return data.ageGroup;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}