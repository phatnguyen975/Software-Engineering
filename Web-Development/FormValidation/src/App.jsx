import BackgroundAnimatedContainer from "./components/BackgroundAnimatedContainer";
import BorderAnimatedContainer from "./components/BorderAnimatedContainer";
import ShippingForm from "./components/ShippingForm";

const App = () => {
  return (
    <BackgroundAnimatedContainer>
      <div className="max-w-lg w-full bg-gray-800 bg-opacity-50 backdrop-filter backdrop-blur rounded-2xl shadow-xl overflow-hidden">
        <BorderAnimatedContainer>
          <ShippingForm />
        </BorderAnimatedContainer>
      </div>
    </BackgroundAnimatedContainer>
  );
};

export default App;
