// import React from "react";
// import axios from "axios";
// import { useState, useEffect } from "react";
// import { useNavigate } from "react-router-dom";
// import TextField from "@mui/material/TextField";
// import Autocomplete from "@mui/material/Autocomplete";
// function SearchQuery() {
//   const [searchQuery, setSearchQuery] = useState("");
//   const navigate = useNavigate();
//   const [clicked, setClicked] = useState(0);
//   const [suggestions, setSuggestions] = useState([]);
//   const setInput = (e) => {
//     const { value } = e.target;
//     setSearchQuery(value);
//   };
//   const sendWords = async () => {
//     try {
//       console.log(searchQuery);
//       if (searchQuery !== "") {
//         navigate("/SearchResults", {
//           state: { sq: searchQuery },
//         });
//       }
//     } catch (e) {
//       console.log(e);
//     }
//   };
//   const getSuggestions = async () => {
//     try {
//       const res = await axios.get(`http://localhost:8080/resultQuery`);
//       if (res.data !== null) {
//         setSuggestions(res.data);
//       }
//     } catch (e) {
//       console.log("error");
//     }
//   };
//   const fetchData = (e) => {
//     e.preventDefault();
//     if (clicked) setClicked(0);
//     else setClicked(1);
//   };
//   useEffect(() => {
//     sendWords();
//   }, [clicked]);
//   useEffect(() => {
//     getSuggestions();
//   }, []);
//   return (
//     <div className="d-flex justify-content-center vh-100">
//       <form
//         className="border-secondary d-flex flex-column align-items-center flex-row justify-content-center w-75"
//         onSubmit={fetchData}
//       >
//         <div className="mb-3 w-100">
//           <Autocomplete
//             freeSolo
//             id="free-solo-2-demo"
//             disableClearable
//             options={suggestions.map((option) => option.key)}
//             renderInput={(params) => (
//               <TextField
//                 {...params}
//                 onChange={setInput}
//                 InputProps={{
//                   ...params.InputProps,
//                   type: "search",
//                   sx: {
//                     backgroundColor: "transparent",
//                     color: "#fff",
//                     width: "75vw",
//                     borderRadius: "20px",
//                     "&:hover": {
//                       backgroundColor: "#40444b",
//                     },
//                     "&.Mui-focused": {
//                       backgroundColor: "#40444b",
//                     },
//                     "& .MuiOutlinedInput-notchedOutline": {
//                       borderColor: "white",
//                     },
//                     "& .MuiInputLabel-outlined": {
//                       display: "none", // hide the label
//                     },

//                     "& .MuiOutlinedInput-input": {
//                       color: "white",
//                     },
//                     "& .MuiInputLabel-outlined": {
//                       color: "white",
//                     },
//                     "& .MuiAutocomplete-endAdornment": {
//                       color: "white",
//                     },
//                     "& .MuiAutocomplete-popupIndicator": {
//                       color: "white",
//                     },
//                     "& .MuiAutocomplete-option": {
//                       color: "white",
//                     },
//                     "& .MuiAutocomplete-noOptions": {
//                       color: "white",
//                     },
//                   },
//                 }}
//               />
//             )}
//           />
//         </div>
//         <button
//           type="button"
//           className="btn btn-light text-dark w-25 gradient-bg"
//           onClick={fetchData}
//         >
//           Search
//         </button>
//       </form>
//     </div>
//   );
// }

// export default SearchQuery;
import React from "react";
import axios from "axios";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import TextField from "@mui/material/TextField";
import Autocomplete from "@mui/material/Autocomplete";

function SearchQuery() {
  const [searchQuery, setSearchQuery] = useState("");
  const navigate = useNavigate();
  const [clicked, setClicked] = useState(0);
  const [suggestions, setSuggestions] = useState([]);

  const setInput = (e, value) => {
    setSearchQuery(value);
  };

  const sendWords = async () => {
    try {
      if (searchQuery !== "") {
        navigate("/SearchResults", {
          state: { sq: searchQuery },
        });
      }
    } catch (e) {
      console.log(e);
    }
  };

  const getSuggestions = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/resultQuery`);
      if (res.data !== null) {
        setSuggestions(res.data);
      }
    } catch (e) {
      console.log("error");
    }
  };

  const fetchData = (e) => {
    e.preventDefault();
    if (clicked) setClicked(0);
    else setClicked(1);
  };

  useEffect(() => {
    sendWords();
  }, [clicked]);

  useEffect(() => {
    getSuggestions();
  }, []);

  return (
    <div className="d-flex justify-content-center vh-100">
      <form
        className="border-secondary d-flex flex-column align-items-center flex-row justify-content-center w-75"
        onSubmit={fetchData}
      >
        <div className="mb-3 w-100">
          <Autocomplete
            freeSolo
            id="free-solo-2-demo"
            disableClearable
            options={suggestions.map((option) => option.key)}
            onChange={setInput}
            renderInput={(params) => (
              <TextField
                {...params}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
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
        </div>
        <button
          type="button"
          className="btn btn-light text-dark w-25 gradient-bg"
          onClick={fetchData}
        >
          Search
        </button>
      </form>
    </div>
  );
}

export default SearchQuery;
