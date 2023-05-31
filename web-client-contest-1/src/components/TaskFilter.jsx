import '../css/TaskFilter.css'
import {useState} from "react";

export default function TaskFilter({filterFunc}) {
    const [data, setData] = useState('');
    const [idx, setIdx] = useState('');

    function handleSubmit(event) {
        event.preventDefault();
        console.log('taskID', idx);
        filterFunc(idx).then(dt => {
            console.log('sdasda', dt);
            setData(dt);
            console.log(dt);
        });
        console.log(data);
    }

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label>
                    <input
                        className="filterTF"
                        type="text"
                        placeholder="Enter the ID here..."
                        onChange={e => setIdx(e.target.value)}
                    />
                </label>
            </div>
            {data.length > 0 && (
                <div className="filtered">
                    {data}
                </div>
            )}
            <input type="submit" className="filter" value="Find the age"/>
        </form>
    );
}