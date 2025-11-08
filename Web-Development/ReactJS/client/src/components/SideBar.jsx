import { useState, useEffect } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { axiosInstance } from "../libs/axios";
import PageLoader from "./PageLoader";

const SideBar = () => {
  const [categories, setCategories] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const [searchParams, setSearchParams] = useSearchParams();

  const selectedCategory = searchParams.get("categoryId") || "";

  const loadCategories = async () => {
    setIsLoading(true);
    setError(null);

    try {
      let res = await axiosInstance.get("/categories");
      const data = res.data;

      for (let i = 0; i < data.length; i++) {
        const category = data[i];
        res = await axiosInstance.get(`/products?categoryId=${category.id}`);
        category.productCount = res.data.length;
      }

      setCategories(data);
    } catch (error) {
      setError(error.message || "Error loading categories");
      console, error("Error loading categories:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  return (
    <aside className="space-y-8">
      <div>
        <h2 className="text-lg font-semibold border-b border-gray-300 pb-2 mb-4">
          Browse Categories
        </h2>
        {!isLoading && !error ? (
          categories.length === 0 ? (
            "No categories"
          ) : (
            <ul className="space-y-2 text-sm">
              {categories.map((category) => (
                <li key={category.id}>
                  <Link
                    to={`/products?categoryId=${category.id}`}
                    className={
                      selectedCategory === category.id
                        ? "!text-orange-400 hover:!text-orange-500 capitalize"
                        : "hover:!text-orange-500 capitalize"
                    }
                    data-discover="true"
                  >
                    {category.name} ({category.productCount || 0})
                  </Link>
                </li>
              ))}
            </ul>
          )
        ) : (
          <>
            {isLoading && <PageLoader />}
            {error && <div className="text-red-500 font-semibold">{error}</div>}
          </>
        )}
      </div>
    </aside>
  );
};

export default SideBar;
