import {useState} from "react";
import '../css/TaskForm.css'


export default function TaskForm({addFunc}) {
    const [taskType, setTaskType] = useState('');
    const [ageGroup, setAgeGroup] = useState('');


    function handleSubmit(event) {
        event.preventDefault();
        let task = {
            type: taskType,
            ageGroup: ageGroup
        }
        console.log('IN HANDLE SUBMIT');
        console.log(task);
        addFunc(task);
    }

    return (
        <form onSubmit={handleSubmit}>
            <table>
                <tbody className="inputWrapper">
                <tr>
                    <td>
                        <label>
                            Type:
                            <input
                                className="taskTypeTF"
                                type="text"
                                value={taskType}
                                onChange={e => setTaskType(e.target.value.toUpperCase())}
                                placeholder="Enter the type here..."
                                required
                            />
                        </label>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label>
                            Age Group:
                            <input
                                className="ageGroupTF"
                                type="text"
                                value={ageGroup}
                                onChange={e => setAgeGroup(e.target.value.toUpperCase())}
                                placeholder="Enter the age here..."
                                required
                            />
                        </label>
                    </td>
                </tr>
                </tbody>
            </table>
            <input type="submit" className="adder" value="Add task "/>
        </form>
    );
}