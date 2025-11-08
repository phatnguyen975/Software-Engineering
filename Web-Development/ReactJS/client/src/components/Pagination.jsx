const Pagination = ({ page, totalPages, onPageChange }) => {
  return (
    totalPages > 1 && (
      <div className="flex gap-2">
        {Array.from({ length: totalPages }, (_, index) => (
          <a
            key={`page${index + 1}`}
            className={`px-3 py-1 cursor-pointer bg-gray-200 rounded ${
              page == index + 1
                ? "bg-orange-500 text-white"
                : "hover:!bg-orange-100"
            }`}
            onClick={() => onPageChange(index + 1)}
          >
            {index + 1}
          </a>
        ))}
      </div>
    )
  );
};

export default Pagination;
