import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import PageLoader from "./PageLoader";
import SideBar from "./SideBar";
import SortBar from "./SortBar";
import Pagination from "./Pagination";
import ProductCard from "./ProductCard";
import { axiosInstance } from "../libs/axios";

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(1);

  const [searchParams, setSearchParams] = useSearchParams();

  const category = searchParams.get("categoryId") || "";
  const sort = searchParams.get("sort") || "";
  const page = searchParams.get("page") || 1;
  const keyword = searchParams.get("keyword") || "";

  const perPage = 3;

  const queryString = useMemo(() => {
    const params = new URLSearchParams();

    params.set("_page", page);
    params.set("_per_page", perPage);

    if (category) {
      params.set("categoryId", category);
    }
    if (sort) {
      params.set("_sort", sort);
    }
    if (keyword) {
      params.set("name", keyword);
    }

    return params.toString();
  }, [category, sort, page]);

  const handleSortChange = (value) => {
    const next = new URLSearchParams(searchParams);

    if (!value) {
      next.delete("sort");
    } else {
      next.set("sort", value);
    }

    next.set("page", 1);
    setSearchParams(next);
  };

  const handlePageChange = (value) => {
    const next = new URLSearchParams(searchParams);

    if (!value) {
      next.delete("page");
    } else {
      next.set("page", value);
    }

    setSearchParams(next);
  };

  const loadProducts = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const res = await axiosInstance.get(`/products?${queryString}`);
      setProducts(res.data.data);
      setTotalPages(res.data.pages);
    } catch (error) {
      setError(error.message || "Error loading products");
      console, error("Error loading products:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
  }, [queryString]);

  return (
    <div className="container mx-auto px-4 py-16 grid md:grid-cols-4 gap-8">
      <SideBar />
      <section className="md:col-span-3 space-y-6">
        <div className="flex items-center justify-center">
          {!isLoading && !error ? (
            products.length === 0 ? (
              "No products"
            ) : (
              <div className="flex flex-col gap-5">
                <div className="flex justify-between items-center border-b border-gray-300 pb-3">
                  <SortBar sort={sort} onSortChange={handleSortChange} />
                  <Pagination
                    page={page}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                  />
                </div>
                <div className="grid sm:grid-cols-2 md:grid-cols-3 gap-6">
                  {products.map((product) => (
                    <ProductCard key={product.id} product={product} />
                  ))}
                </div>
              </div>
            )
          ) : (
            <div className="flex items-center justify-center">
              {isLoading && <PageLoader />}
              {error && (
                <div className="text-red-500 font-semibold">{error}</div>
              )}
            </div>
          )}
        </div>
      </section>
    </div>
  );
};

export default ProductList;
