function Pagination(props) {
  const { currentPage, resultsPerPage, totalResults, onPageChange } = props;
  const pageNumbers = [];

  for (let i = 1; i <= Math.ceil(totalResults / resultsPerPage); i++) {
    pageNumbers.push(i);
  }

  return (
    <nav>
      <ul className="pagination">
        {pageNumbers.map((pageNumber) => (
          <li
            key={pageNumber}
            className={`page-item ${
              currentPage === pageNumber ? "active" : ""
            }`}
          >
            <button
              className="page-link"
              onClick={() => onPageChange(pageNumber)}
            >
              {pageNumber}
            </button>
          </li>
        ))}
      </ul>
    </nav>
  );
}
