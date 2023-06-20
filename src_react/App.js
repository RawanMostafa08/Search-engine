import "./App.css";
import bgImage from "./Assets/bgImage2.png";
import Navbar from "./Components/Navbar";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import SearchQuery from "./Components/SearchQuery";
import SearchResult from "./Components/SearchResult";
function App() {
  return (
    <Router>
      <div className="App bg-dark">
        <Navbar />
        <Routes>
          <Route path="/" element={<SearchQuery />} />
          <Route path="/SearchResults" element={<SearchResult />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
