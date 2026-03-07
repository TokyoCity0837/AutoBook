import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import Autobook from "./Autobook";
import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Autobook />
  </StrictMode>
);