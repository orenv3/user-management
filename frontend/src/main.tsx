import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import { loadRuntimeConfig } from "./config/runtimeConfig";
import "./styles.css";

async function bootstrap() {
  await loadRuntimeConfig();

  ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </React.StrictMode>
  );
}

void bootstrap();
