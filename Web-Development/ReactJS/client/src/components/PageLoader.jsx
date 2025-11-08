import { Loader2 } from "lucide-react";

function PageLoader() {
  return (
    <div className="flex items-center justify-center">
      <Loader2
        className="size-10 animate-spin text-orange-400 rounded-full"
        strokeWidth={1.5}
      />
    </div>
  );
}

export default PageLoader;
