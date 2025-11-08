import { Link } from "react-router-dom";
import { ArrowRight } from "lucide-react";

const NotFoundPage = () => {
  return (
    <div className="h-[85vh] flex flex-col items-center justify-center gap-8 max-md:px-4 py-20">
      <h1 className="text-4xl md:text-5xl font-bold">404 Not Found</h1>
      <p className="md:text-xl text-slate-500 max-w-xl text-center">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link
        to="/"
        className="group bg-gray-200 hover:bg-gray-300 flex items-center gap-1 px-7 py-2.5 text-gray-800 rounded-full font-medium active:scale-95 transition-all"
      >
        Back to Home
        <ArrowRight
          className="size-4.5 group-hover:translate-x-0.5 transition"
          strokeWidth={1.5}
        />
      </Link>
    </div>
  );
};

export default NotFoundPage;
