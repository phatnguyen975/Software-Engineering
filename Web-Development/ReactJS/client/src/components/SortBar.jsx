import { useEffect, useRef, useState } from "react";
import { ChevronDown, ChevronRight } from "lucide-react";

const sortOptions = [
  { name: "Sort by Default", value: "" },
  { name: "Sort by Price", value: "price" },
  { name: "Sort by Rating", value: "-stars" },
];

const SortBar = ({ value, onChange }) => {
  const [sortMenuOpen, setSortMenuOpen] = useState(false);

  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setSortMenuOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <>
      {/* Desktop Sortbar */}
      <div className="hidden sm:flex">
        <div className="relative flex items-center text-sm">
          <select
            className="appearance-none border border-gray-400 rounded-xl pl-3 pr-9 py-1.5"
            value={value}
            onChange={(e) => onChange(e.target.value)}
          >
            {sortOptions.map((opt) => (
              <option key={opt.name} value={opt.value}>
                {opt.name}
              </option>
            ))}
          </select>
          <div className="absolute right-3">
            <ChevronDown className="size-4" />
          </div>
        </div>
      </div>

      {/* Mobile Sortbar */}
      <div className="sm:hidden flex relative" ref={menuRef}>
        <button
          className={`flex items-center gap-1 text-sm px-2 py-1.5 cursor-pointer border ${
            sortMenuOpen
              ? "border-gray-200 bg-gray-200 rounded-t-lg"
              : "border-gray-300 rounded-lg"
          }`}
          onClick={() => setSortMenuOpen((prev) => !prev)}
        >
          Sort by
          <ChevronRight
            className={`size-4 ${
              sortMenuOpen && "rotate-90 transition-transform"
            }`}
          />
        </button>

        {/* Dropdown */}
        {sortMenuOpen && (
          <div className="absolute top-full right-0 min-w-[200px] bg-white border border-gray-300 rounded-b-lg rounded-tl-lg shadow-sm z-50">
            <div className="flex flex-col gap-2 items-start p-2">
              {sortOptions.map((opt) => (
                <button
                  key={opt.value}
                  className="w-full text-left hover:bg-gray-200 px-2 py-1 rounded-md cursor-pointer"
                  onClick={() => {
                    onChange(opt.value);
                    setSortMenuOpen(false);
                  }}
                >
                  {opt.name}
                </button>
              ))}
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default SortBar;
