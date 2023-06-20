import { useState, useEffect } from "react";
function OneResult(props) {
  const [tag, setTag] = useState([" "]);
  useEffect(() => {
    setTag(props.tag.split(" "));
  }, [props.url]);

  return (
    <>
      <div className=" box-color d-flex align-items-start flex-column p-2 border-bottom border-secondary border-4 rounded-2 m-2 bg-div">
        <div className="mask" />
        <a href={props.url} target="_blank" className=" title-color">
          {props.title}
        </a>
        <a href={props.url} target="_blank" className="url-color">
          {props.url}
        </a>
        <div style={{ textAlign: "left" }}>
  {tag.map((t, index) => {
    if (props.indeces.includes(index)) {
      return (
        <span className="description-color" style={{ fontWeight: "bolder" }}>
          {t}{" "}
        </span>
      );
    } else {
      return <span className="description-color">{t} </span>;
    }
  })}
</div>
       
      </div>
    </>
  );
}
export default OneResult;
