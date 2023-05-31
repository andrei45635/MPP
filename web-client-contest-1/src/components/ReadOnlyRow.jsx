import React from 'react'
import '../css/ROR.css'

function ReadOnlyRow({task, handleEditClick, handleDeleteClick}) {
    return (
        <tr>
            <td>{task.id}</td>
            <td>{task.type}</td>
            <td>{task.ageGroup}</td>
            <td>
                <button className="modBtn" type="button" onClick={event => {
                    handleEditClick(event, task)
                }}>
                    Modify
                </button>
                <button className="delBtn" type="button" onClick={event => {
                    handleDeleteClick(event, task);
                }}>
                    Delete
                </button>
            </td>
        </tr>
    );
}

export default ReadOnlyRow;