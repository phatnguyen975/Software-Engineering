const SortBar = ({ sort, onSortChange }) => {
  const sortOptions = [
    { name: "Sort by Default", value: "" },
    { name: "Price", value: "price" },
    { name: "Rating", value: "-stars" },
  ];

  return (
    <select
      className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg !focus:ring-orange-500 !focus:border-orange-500 block w-1/4 px-2 py-1 text-sm"
      onChange={(e) => onSortChange(e.target.value)}
      value={sort}
    >
      {sortOptions.map((option) => (
        <option key={option.name} value={option.value}>
          {option.name}
        </option>
      ))}
    </select>
  );
};

export default SortBar;
