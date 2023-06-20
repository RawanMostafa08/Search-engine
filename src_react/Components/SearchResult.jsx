import { useState, useEffect } from "react";
import OneResult from "./OneResult";
import axios from "axios";
import { useLocation } from "react-router-dom";
import TextField from "@mui/material/TextField";
import Autocomplete from "@mui/material/Autocomplete";

function SearchResult() {
  const location = useLocation();
  const [data, setData] = useState("");
  const [t1, sett1] = useState(0);
  const [t2, sett2] = useState(0);
  const [initial, setInitial] = useState(1);
  const [posted, setPosted] = useState(0);
  const [searchQuery, setSearchQuery] = useState(location.state.sq);
  const [currentPage, setCurrentPage] = useState(1);
  const [suggestions, setSuggestions] = useState([]);
  const resultsPerPage = 10;
  let postTime;
  let getTime;
  let diff;
  const setInput = (e) => {
    const { value } = e.target;
    setSearchQuery(value);
  };

  const sendWords = async () => {
    try {
      console.log(searchQuery);
      if ((searchQuery !== "") & (searchQuery !== 0)) {
        sett1(startTime);
        await axios.post("http://localhost:8080/my-endpoint", searchQuery);
        setPosted(!posted);
        if (initial === 1) {
          setInitial(0);
        } else {
          setData("");
        }
      }
    } catch (e) {
      console.log("error");
      console.log(e);
      setData([]);
      handletime();
    }
  };
  const sendSuggestions = async () => {
    try {
      if (searchQuery !== "") {
        await axios.post("http://localhost:8080/query", searchQuery);
      }
    } catch (e) {
      console.log("error");
      console.log(e);
    }
  };

  const handlePost = (e) => {
    e.preventDefault();
    sendWords();
    sendSuggestions();
  };

  useEffect(() => {
    getWords();
    getSuggestions();
  }, [posted]);

  useEffect(() => {
    sendWords();
  }, []);

  const startTime = () => {
    const d = new Date();
    return d.getTime();
  };

  const handletime = () => {
    sett1(0);
    sett2(0);
  };

  const getWords = async () => {
    try {
      sett2(startTime);
      const res = await axios.get(`http://localhost:8080/result`);
      setSearchQuery("");
      if (res.data !== null) {
        setData([]);
        res.data.map((object) => {
          setData((data) => [
            ...data,
            {
              url: object.docName,
              title: object.DocTitle,
              tag: object.showntag,
              words: object.Words,
              indeces: object.indeces,
            },
          ]);
        });
      }
    } catch (e) {
      handletime();
      console.log("error");
    }
  };

  const getSuggestions = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/resultQuery`);
      if (res.data !== null) {
        setSuggestions(res.data);
      }
    } catch (e) {
      handletime();
      console.log("error");
    }
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleAutocompleteChange = (event, value) => {
    setSearchQuery(value || "");
  };
  const indexOfLastResult = currentPage * resultsPerPage;
  const indexOfFirstResult = indexOfLastResult - resultsPerPage;
  const currentResults = data?.slice(indexOfFirstResult, indexOfLastResult);

  return (
    <>
      <div className="bg-dark vh-100" style={{ overflow: "hidden auto" }}>
        <style>
          {`
      ::-webkit-scrollbar {
        display: none;
      }
    `}
        </style>

        <div className="d-flex justify-content-start  mb-3 mt-3 text-white p-2">
          <form
            className="search-input w-75 border-secondary d-flex flex-column align-items-center flex-row justify-content-center w-75"
            onSubmit={handlePost}
          >
            <Autocomplete
              freeSolo
              id="free-solo-2-demo"
              disableClearable
              options={suggestions.map((option) => option.key)}
              onChange={handleAutocompleteChange}
              renderInput={(params) => (
                <TextField
                  {...params}
                  onChange={setInput}
                  InputProps={{
                    ...params.InputProps,
                    type: "search",
                    sx: {
                      backgroundColor: "transparent",
                      color: "#fff",
                      width: "75vw",
                      borderRadius: "20px",
                      "&:hover": {
                        backgroundColor: "#40444b",
                      },
                      "&.Mui-focused": {
                        backgroundColor: "#40444b",
                      },
                      "& .MuiOutlinedInput-notchedOutline": {
                        borderColor: "white",
                      },
                      "& .MuiInputLabel-outlined": {
                        display: "none", // hide the label
                      },

                      "& .MuiOutlinedInput-input": {
                        color: "white",
                      },
                      "& .MuiInputLabel-outlined": {
                        color: "white",
                      },
                      "& .MuiAutocomplete-endAdornment": {
                        color: "white",
                      },
                      "& .MuiAutocomplete-popupIndicator": {
                        color: "white",
                      },
                      "& .MuiAutocomplete-option": {
                        color: "white",
                      },
                      "& .MuiAutocomplete-noOptions": {
                        color: "white",
                      },
                    },
                  }}
                />
              )}
            />
          </form>
        </div>

        {data !== null && data !== undefined && data !== "" ? (
          <>
            <span className="description-color">
              {data.length} results found in {t2 - t1} millisec{handletime}
            </span>
            {currentResults.map((doc) => (
              <OneResult
                title={doc.title}
                url={doc.url}
                tag={doc.tag}
                indeces={doc.indeces}
              />
            ))}
            <div className="d-flex justify-content-center">
              <nav>
                <ul className="pagination">
                  {[...Array(Math.ceil(data.length / resultsPerPage))].map(
                    (x, i) => (
                      <li
                        key={i}
                        className={
                          currentPage === i + 1
                            ? "page-item active"
                            : "page-item"
                        }
                      >
                        <button
                          className="page-link"
                          onClick={() => handlePageChange(i + 1)}
                        >
                          {i + 1}
                        </button>
                      </li>
                    )
                  )}
                </ul>
              </nav>
            </div>
          </>
        ) : (
          <div className="d-flex justify-content-center align-items-center vh-100">
            <h1 className="gradient-text-inf">Loading</h1>
          </div>
        )}
      </div>
    </>
  );
}

export default SearchResult;
