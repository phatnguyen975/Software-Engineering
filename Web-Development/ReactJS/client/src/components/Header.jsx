import { Link, useNavigate } from "react-router-dom";
import { assets } from "../assets/assets";
import { useState } from "react";

const Header = ({ title = "" }) => {
  const [keyword, setKeyword] = useState("");
  const navigate = useNavigate();

  const handleSearch = (e) => {
    e.preventDefault();

    if (keyword.trim()) {
      navigate(`/products?keyword=${keyword.trim()}`);
      setKeyword("");
    }
  };

  return (
    <header className="bg-[url('/banner.jpg')] text-white bg-cover bg-center py-8">
      <nav className="container mx-auto bg-white border-gray-200 rounded-lg">
        <div className="max-w-screen-xl flex flex-wrap items-center justify-between mx-auto p-4">
          <Link
            to="/"
            className="flex items-center space-x-3 rtl:space-x-reverse"
            data-discover="true"
          >
            <img className="h-8" alt="Karma Logo" src={assets.logo} />
          </Link>
          <div className="flex md:order-2">
            <button
              type="button"
              data-collapse-toggle="navbar-search"
              aria-controls="navbar-search"
              aria-expanded="false"
              className="md:hidden text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-4 focus:ring-gray-200 rounded-lg text-sm p-2.5 me-1"
            >
              <svg
                className="w-5 h-5"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 20 20"
              >
                <path
                  stroke="currentColor"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                />
              </svg>
              <span className="sr-only">Search</span>
            </button>
            <div className="relative hidden md:block">
              <form onSubmit={handleSearch}>
                <div className="absolute inset-y-0 end-3 flex items-center ps-3 pointer-events-none">
                  <svg
                    className="w-4 h-4 text-gray-500"
                    aria-hidden="true"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 20 20"
                  >
                    <path
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                    />
                  </svg>
                  <span className="sr-only">Search icon</span>
                </div>
                <input
                  className="block text-sm focus:border-orange-600 w-full border !border-orange-400 rounded-full py-2.5 px-5 text-gray-800 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-orange-500"
                  placeholder="Search..."
                  type="text"
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                />
              </form>
            </div>
          </div>
          <div
            className="items-center justify-between hidden w-full md:flex md:w-auto md:order-1"
            id="navbar-search"
          >
            <div className="relative mt-3 md:hidden">
              <form onSubmit={handleSearch}>
                <div className="absolute inset-y-0 end-3 flex items-center ps-3 pointer-events-none">
                  <svg
                    className="w-4 h-4 text-gray-500"
                    aria-hidden="true"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 20 20"
                  >
                    <path
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                    />
                  </svg>
                </div>
                <input
                  className="block text-sm focus:border-orange-600 w-full border !border-orange-400 rounded-full py-2.5 px-5 text-gray-800 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-orange-500"
                  placeholder="Search..."
                  type="text"
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                />
              </form>
            </div>
          </div>
        </div>
      </nav>
      <div className="container hidden md:block mx-auto text-end px-8 py-28 relative z-0">
        <h1 className="text-4xl font-bold mb-2">Karma Shop</h1>
        <nav className="text-sm">
          <Link
            to="/"
            className="hover:underline font-bold"
            data-discover="true"
          >
            Home
          </Link>
          {title && (
            <>
              <svg
                className="w-6 h-6 inline"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  stroke="currentColor"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M19 12H5m14 0-4 4m4-4-4-4"
                ></path>
              </svg>
              <Link to="/" className="font-bold">
                {title}
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
