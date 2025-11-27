import { Link } from "react-router-dom";
import { ArrowRight, Heart, Info, Minus, Plus, X } from "lucide-react";
import { useAppContext } from "../context/AppContext";
import { useState } from "react";

const ShoppingCart = () => {
  const { cart, totalPrice, updateQuantity, removeFromCart } = useAppContext();

  const [modalOpen, setModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  const handleRemoveClick = (item) => {
    setSelectedItem(item);
    setModalOpen(true);
  };

  const confirmRemove = () => {
    if (selectedItem) {
      removeFromCart(selectedItem.id);
      selectedItem(null);
    }
    setModalOpen(false);
  }

  const cancelRemove = () => {
    setSelectedItem(null);
    setModalOpen(false);
  };

  return (
    <section className="bg-white py-8 antialiased dark:bg-gray-900 md:py-16">
      <div className="mx-auto max-w-7xl px-4 2xl:px-0">
        {cart.length === 0 ? (
          <p className="text-center">Your cart is empty</p>
        ) : (
          <div className="mt-6 sm:mt-8 md:gap-6 lg:flex lg:items-start xl:gap-8">
            {/* Left - Cart */}
            <div className="mx-auto w-full flex-none lg:max-w-2xl xl:max-w-4xl">
              <div className="space-y-6">
                {cart.map((item) => (
                  <div
                    key={item.name}
                    className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-800 md:p-6"
                  >
                    <div className="space-y-4 md:flex md:items-center md:justify-between md:gap-6 md:space-y-0">
                      <Link
                        to={`/products/${item.id}`}
                        className="shrink-0 md:order-1"
                      >
                        <img
                          className="h-20 w-20 dark:hidden"
                          src={item.imagePath}
                          alt={item.name}
                        />
                        <img
                          className="hidden h-20 w-20 dark:block"
                          src={item.imagePath}
                          alt={item.name}
                        />
                      </Link>
                      <label htmlFor="counter-input" className="sr-only">
                        Choose quantity:
                      </label>
                      <div className="flex items-center justify-between md:order-3 md:justify-end">
                        <div className="flex items-center">
                          <button
                            type="button"
                            id="decrement-button"
                            data-input-counter-decrement="counter-input"
                            className="inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-md border border-gray-300 bg-gray-100 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-100 dark:border-gray-600 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700"
                            onClick={() =>
                              updateQuantity(item.id, item.quantity - 1)
                            }
                            disabled={item.quantity <= 1}
                          >
                            <Minus className="size-3.5" />
                          </button>
                          <input
                            type="text"
                            id="counter-input"
                            data-input-counter
                            className="w-10 shrink-0 border-0 bg-transparent text-center text-sm font-medium text-gray-900 focus:outline-none focus:ring-0 dark:text-white"
                            value={item.quantity}
                            required
                            readOnly
                          />
                          <button
                            type="button"
                            id="increment-button"
                            data-input-counter-increment="counter-input"
                            className="inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-md border border-gray-300 bg-gray-100 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-100 dark:border-gray-600 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700"
                            onClick={() =>
                              updateQuantity(item.id, item.quantity + 1)
                            }
                            disabled={item.quantity >= 10}
                          >
                            <Plus className="size-3.5" />
                          </button>
                        </div>
                        <div className="text-end md:order-4 md:w-32">
                          <p className="text-base font-bold text-gray-900 dark:text-white">
                            $ {(item.price * item.quantity).toFixed(2)}
                          </p>
                        </div>
                      </div>
                      <div className="w-full min-w-0 flex-1 space-y-4 md:order-2 md:max-w-md">
                        <Link
                          to={`/products/${item.id}`}
                          className="text-base font-medium text-gray-900 hover:underline dark:text-white"
                        >
                          {item.name}
                        </Link>
                        <div className="flex items-center gap-4">
                          <button
                            type="button"
                            className="inline-flex gap-1 items-center text-sm font-medium text-gray-500 hover:text-gray-900 hover:underline dark:text-gray-400 dark:hover:text-white"
                          >
                            <Heart className="size-4" />
                            Add to Favorites
                          </button>
                          <button
                            type="button"
                            className="inline-flex gap-1 items-center text-sm font-medium text-red-600 hover:underline dark:text-red-500"
                            onClick={() => handleRemoveClick(item)}
                          >
                            <X className="size-4" />
                            Remove
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Right - Summary */}
            <div className="mx-auto mt-6 max-w-4xl flex-1 space-y-6 lg:mt-0 lg:w-full">
              <div className="space-y-4 rounded-lg border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-800 sm:p-6">
                <p className="text-xl font-semibold text-gray-900 dark:text-white">
                  Order summary
                </p>
                <div className="space-y-4">
                  <dl className="flex items-center justify-between gap-4 border-t border-gray-200 pt-2 dark:border-gray-700">
                    <dt className="text-base font-bold text-gray-900 dark:text-white">
                      Total
                    </dt>
                    <dd className="text-base font-bold text-gray-900 dark:text-white">
                      $ {totalPrice.toFixed(2)}
                    </dd>
                  </dl>
                </div>
                <a
                  href="#"
                  className="flex w-full items-center justify-center rounded-lg bg-orange-500 px-5 py-2.5 text-sm font-medium text-white hover:!bg-orange-600 focus:outline-none focus:ring-4"
                >
                  Proceed to Checkout
                </a>
                <div className="flex items-center justify-center gap-2">
                  <span className="text-sm font-normal text-gray-500 dark:text-gray-400">
                    {" "}
                    or{" "}
                  </span>
                  <Link
                    to="/"
                    className="inline-flex items-center gap-2 text-sm font-medium text-primary-700 underline hover:no-underline dark:text-primary-500"
                  >
                    Continue Shopping
                    <ArrowRight className="size-4" />
                  </Link>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Confirm Modal */}
      {modalOpen && (
        <div
          id="popup-modal"
          tabIndex={-1}
          className="flex bg-black/50 backdrop-blur-sm overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
          onClick={cancelRemove}
        >
          <div className="relative p-4 w-full max-w-md max-h-full">
            <div className="relative rounded-lg bg-white border border-default rounded-base shadow-sm p-4 md:p-6">
              <button
                type="button"
                className="absolute top-3 end-2.5 text-body bg-transparent hover:!text-red-500 hover:text-heading rounded-base text-sm w-9 h-9 ms-auto inline-flex justify-center items-center"
                data-modal-hide="popup-modal"
                onClick={cancelRemove}
              >
                <X className="size-5" />
              </button>
              <div className="p-4 md:p-5 flex flex-col items-center gap-4 text-center">
                <Info className="size-12" />
                <h3 className="mb-6 text-body">
                  Are you sure you want to delete <strong>{selectedItem.name}</strong> from your cart?
                </h3>
                <div className="flex items-center space-x-4 justify-center">
                  <button
                    data-modal-hide="popup-modal"
                    type="button"
                    className="rounded-lg text-white bg-red-500 box-border border border-transparent hover:bg-danger-strong focus:ring-4 focus:ring-danger-medium shadow-xs font-medium leading-5 rounded-base text-sm px-4 py-2.5 focus:outline-none"
                    onClick={confirmRemove}
                  >
                    Yes, I'm sure
                  </button>
                  <button
                    data-modal-hide="popup-modal"
                    type="button"
                    className="rounded-lg text-body bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5 rounded-base text-sm px-4 py-2.5 focus:outline-none"
                    onClick={cancelRemove}
                  >
                    No, cancel
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

export default ShoppingCart;
