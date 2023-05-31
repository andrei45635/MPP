import React, {Fragment, useState} from 'react';
import ReadOnlyRow from "./ReadOnlyRow.jsx";
import EditableRow from "./EditableRow.jsx";

export default function TaskTable({tasks, deleteFunc, modFunc}) {
    console.log('taskuri', tasks);
    const [editID, setEditID] = useState(null);
    const [editFormData, setEditFormData] = useState({
        id: 0,
        type: "",
        ageGroup: ""
    });

    function handleDeleteClick(event, task) {
        console.log('Delete button for the task ', task.id);
        deleteFunc(task.id);
    }

    function handleCancelClick(){
        setEditID(null);
    }

    function handleEditFormChange(event) {
        event.preventDefault();

        const fieldName = event.target.getAttribute("name");
        const fieldValue = event.target.value.toUpperCase();
        console.log('form data', editFormData);
        const newFormData = {...editFormData};
        newFormData[fieldName] = fieldValue;

        setEditFormData(newFormData);
    }

    function handleEditClick(event, task) {
        event.preventDefault();
        setEditID(task.id);
        console.log('EDIT ID!!!!', task.id);
        const formValues = {
            id: task.id,
            type: task.type,
            ageGroup: task.ageGroup
        }

        setEditFormData(formValues);
    }

    function handleEditFormSubmit(event) {
        event.preventDefault();

        const editedTask = {
            id: editID,
            type: editFormData.type,
            ageGroup: editFormData.ageGroup
        }

        const idx = tasks.findIndex((task) => task.id === editID);
        console.log('%c IMPORTANT', 'style=color:red');
        console.log('idx', idx, 'editid', editID);
        modFunc(editID, editedTask);
        setEditID(null);
    }

    return (
        <div className="TaskTable">
            <form onSubmit={handleEditFormSubmit}>
                <table className="center">
                    <thead>
                    <tr className="taskRows">
                        <th>Task ID</th>
                        <th>Type</th>
                        <th>Age Group</th>
                        <th colSpan="2">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="taskBody">
                    {tasks.map((task) => (
                        <Fragment key={task.id}>
                            {editID === task.id ? (
                                <EditableRow editFormData={editFormData}
                                             handleEditFormChange={handleEditFormChange} handleCancelClick={handleCancelClick}></EditableRow>
                            ) : (
                                <ReadOnlyRow task={task} handleEditClick={handleEditClick} handleDeleteClick={handleDeleteClick}></ReadOnlyRow>
                            )}
                        </Fragment>
                    ))}
                    </tbody>
                </table>
            </form>
        </div>
    );
}
