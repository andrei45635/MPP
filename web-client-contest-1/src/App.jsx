import {useEffect, useState} from 'react'
import './css/App.css'
import {AddTask, DeleteTask, GetAgeGroup, GetTasks, ModifyTask} from "./utils/rest-calls.js";
import TaskTable from "./components/TaskTable.jsx";
import TaskForm from "./components/TaskForm.jsx";
import TaskFilter from "./components/TaskFilter.jsx";

function App() {
    const [tasks, setTasks] = useState([]);

    useEffect(function () {
        console.log('inside useEffect')
        GetTasks().then(tasks => setTasks(tasks));
    }, []);

    function deleteFunc(task) {
        console.log('inside deleteFunc ', task);
        DeleteTask(task)
            .then(() => GetTasks())
            .then(tasks => setTasks(tasks))
            .catch(error => console.log('Error when deleting ', error));
    }

    function modFunc(id, task) {
        console.log('inside modify function ', id, task);
        ModifyTask(id, task)
            .then(() => GetTasks())
            .then(tasks => setTasks(tasks))
            .catch(error => console.log('Error when modifying ', error));
    }

    function filterFunc(id) {
        console.log('inside get age group!!!', id);
        console.log(GetAgeGroup(id));
        return GetAgeGroup(id);
    }

    function mgs3() {
        window.open('https://www.konami.com/mg/mgs3r/eu/en/', '_blank');
    }

    function addFunc(task) {
        console.log('inside addFunc ', task);
        AddTask(task)
            .then(() => GetTasks())
            .then(tasks => setTasks(tasks))
            .catch(error => console.log('Error when adding ', error));
    }

    return (<div className="App">
        <h1> Tasks Web Client
            <span className="delta" onClick={mgs3}> Δ </span>
        </h1>
        <div className="divider" style={{display: 'flex'}}>
            <div style={{flex: 1}}>
                <TaskForm addFunc={addFunc}></TaskForm>
                <br/>
            </div>
        </div>
        <div>
            <TaskFilter filterFunc={filterFunc}></TaskFilter>
        </div>
        <br/>
        <TaskTable tasks={tasks} deleteFunc={deleteFunc} modFunc={modFunc}/>
    </div>);
}

export default App
