import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import Autobook from "./app/Autobook";
import "./assets/styles/index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Autobook />
  </StrictMode>
);