import { useState, useEffect } from "react";
import { z } from "zod";
import provinceData from "../assets/province.json";
import wardData from "../assets/ward.json";

const allProvinces = Object.values(provinceData);
const allWards = Object.values(wardData);

const shippingSchema = z.object({
  houseNumber: z
    .string()
    .min(1, { message: "House number is required" })
    .max(20, { message: "House number must be 20 characters or less" }),
  street: z
    .string()
    .min(1, { message: "Street is required" })
    .max(100, { message: "Street must be 100 characters or less" }),
  province: z.string().min(1, { message: "Please select a city/province" }),
  ward: z.string().min(1, { message: "Please select a ward" }),
});

const ShippingForm = () => {
  const [formData, setFormData] = useState({
    houseNumber: "",
    street: "",
    province: "",
    ward: "",
  });

  const [filteredWards, setFilteredWards] = useState([]);
  const [errors, setErrors] = useState(null);

  useEffect(() => {
    if (formData.province) {
      const relevantWards = allWards.filter(
        (ward) => ward.parent_code === formData.province
      );
      setFilteredWards(relevantWards);
    } else {
      setFilteredWards([]);
    }
  }, [formData.province]);

  const handleProvinceChange = (e) => {
    const newProvinceCode = e.target.value;
    setFormData((prev) => ({
      ...prev,
      province: newProvinceCode,
      ward: "",
    }));
    setErrors(null);
  };

  const handleChange = (e) => {
    const { id, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [id]: value,
    }));
    setErrors(null);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setErrors(null);

    const result = shippingSchema.safeParse(formData);

    if (!result.success) {
      const formattedErrors = result.error.format();
      setErrors(formattedErrors);
      return;
    }

    const provinceName =
      allProvinces.find((province) => province.code === result.data.province)
        ?.name || "None";
    const wardName =
      allWards.find((ward) => ward.code === result.data.ward)?.name || "None";

    alert(
      "Shipping Information\n" +
        `- House Number: ${result.data.houseNumber}\n` +
        `- Street: ${result.data.street}\n` +
        `- Province: ${provinceName}\n` +
        `- Ward: ${wardName}`
    );
  };

  return (
    <form
      className="w-full p-8 gap-6 flex flex-col items-center justify-center"
      onSubmit={handleSubmit}
    >
      <h2 className="text-3xl font-bold mb-4 text-cyan-600">Shipping Form</h2>

      {/* House Number */}
      <div className="flex w-full items-center">
        <label
          htmlFor="houseNumber"
          className="w-[35%] text-cyan-600 font-bold items-center"
        >
          House Number:
        </label>
        <input
          id="houseNumber"
          type="text"
          placeholder="Enter your house number"
          value={formData.houseNumber}
          onChange={handleChange}
          className="w-[65%] border border-cyan-600 bg-slate-900/70 px-4 py-2 rounded-lg"
        />
      </div>

      {/* Street */}
      <div className="flex w-full items-center">
        <label
          htmlFor="street"
          className="w-[35%] text-cyan-600 font-bold items-center"
        >
          Street:
        </label>
        <input
          id="street"
          type="text"
          placeholder="Enter your street"
          value={formData.street}
          onChange={handleChange}
          className="w-[65%] border border-cyan-600 bg-slate-900/70 px-4 py-2 rounded-lg"
        />
      </div>

      {/* Province */}
      <div className="w-full flex">
        <label
          htmlFor="province"
          className="w-[35%] text-cyan-600 font-bold items-center"
        >
          City/Province:
        </label>
        <select
          id="province"
          value={formData.province}
          onChange={handleProvinceChange}
          className="w-[65%] border bg-slate-900/70 border-cyan-600 px-4 py-2 rounded-lg items-center"
        >
          <option value="">-- Select City --</option>
          {allProvinces.map((province) => (
            <option key={province.code} value={province.code}>
              {province.name}
            </option>
          ))}
        </select>
      </div>

      {/* Ward */}
      <div className="w-full flex">
        <label
          htmlFor="ward"
          className="w-[35%] text-cyan-600 font-bold items-center"
        >
          Ward:
        </label>
        <select
          id="ward"
          value={formData.ward}
          onChange={handleChange}
          className="w-[65%] border bg-slate-900/70 border-cyan-600 px-4 py-2 rounded-lg items-center disabled:bg-slate-700/70 disabled:cursor-not-allowed"
          disabled={!formData.province}
        >
          <option value="">-- Select Ward --</option>
          {filteredWards.map((ward) => (
            <option key={ward.code} value={ward.code}>
              {ward.name_with_type}
            </option>
          ))}
        </select>
      </div>

      {/* Error */}
      {errors && (
        <div className="w-full px-4 py-2 bg-red-200 border-2 border-red-500 text-red-600 rounded-lg text-sm">
          <ul className="list-disc list-inside">
            {Object.keys(errors).map((field) => {
              if (field !== "_errors") {
                return errors[field]?._errors.map((error, index) => (
                  <li key={`${field}-${index}`}>{error}</li>
                ));
              }
              return null;
            })}
          </ul>
        </div>
      )}

      <button
        type="submit"
        className="w-full items-center text-white rounded-lg cursor-pointer px-6 py-3 text-xl font-bold bg-cyan-600 hover:bg-cyan-700"
      >
        Submit
      </button>
    </form>
  );
};

export default ShippingForm;
