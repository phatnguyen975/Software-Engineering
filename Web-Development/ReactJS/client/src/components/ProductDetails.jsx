import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import StarRating from "./StarRating";
import PageLoader from "./PageLoader";
import { axiosInstance } from "../lib/axios";
import { useAppContext } from "../context/AppContext";

const ProductDetails = () => {
  const { addToCart } = useAppContext();

  const [product, setProduct] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [quantity, setQuantity] = useState(1);

  const { id } = useParams();

  const loadProduct = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const res = await axiosInstance.get(`/products/${id}`);
      setProduct(res.data);
    } catch (error) {
      setError(error.message || "Error loading product");
      console.error("Error loading product:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadProduct();
  }, [id]);

  return (
    <div className="flex items-center justify-center">
      {isLoading && <PageLoader />}
      {error && <div className="text-red-500 font-semibold">{error}</div>}
      {!isLoading && !error && (
        <section className="container mx-auto py-16 px-4 grid md:grid-cols-2 gap-10">
          <div>
            <img
              alt={product.name}
              className="w-full"
              src={product.imagePath}
            />
          </div>
          <div>
            <h3 className="text-2xl font-semibold mb-2">{product.name}</h3>
            <h2 className="text-3xl font-bold text-orange-600 mb-3">
              ${product.price}
            </h2>
            <StarRating rating={product.stars} />
            <p className="text-gray-600 my-6">{product.summary}</p>
            <div className="flex items-center mb-6">
              <label htmlFor="qty" className="mr-3 font-medium">
                Quantity:
              </label>
              <input
                id="qty"
                min={1}
                max={10}
                className="w-20 border rounded text-center"
                type="number"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
              />
            </div>
            <button
              className="bg-orange-500 text-white px-6 py-2 rounded hover:bg-orange-600"
              onClick={() => addToCart(product, quantity)}
            >
              Add to Cart
            </button>
          </div>
        </section>
      )}
    </div>
  );
};

export default ProductDetails;
