const Footer = () => {
  return (
    <footer className="bg-gray-900 text-gray-400 text-sm mt-16">
      <div className="py-4 border-t border-gray-800">
        <p className="flex gap-1 justify-center items-center">
          &copy; {new Date().getFullYear()} All rights reserved | Made with ❤️
          by
          <a href="#" className="text-orange-500 hover:underline">
            Colorlib
          </a>
        </p>
      </div>
    </footer>
  );
};

export default Footer;
