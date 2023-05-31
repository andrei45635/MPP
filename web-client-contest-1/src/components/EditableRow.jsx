import React from 'react';
import '../css/ER.css';

function EditableRow({editFormData, handleEditFormChange, handleCancelClick}) {
    return (
        <tr className="editInputs">
            <td>
                <input
                    type="text"
                    required="required"
                    placeholder="Enter a new type..."
                    name="type"
                    value={editFormData.type}
                    onChange={handleEditFormChange}
                    className="editType"
                ></input>
            </td>
            <td>
                <input
                    type="text"
                    required="required"
                    placeholder="Enter a new age group..."
                    name="ageGroup"
                    value={editFormData.ageGroup}
                    onChange={handleEditFormChange}
                    className="editAge"
                ></input>
            </td>
            <td>
                <button type="submit">Save</button>
                <button type="submit" onClick={handleCancelClick}>Cancel</button>
            </td>
        </tr>
    );
}

export default EditableRow;