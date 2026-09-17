import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "https://codelens-backend-835772257876.us-central1.run.app",
        changeOrigin: true,
        secure: true,
      },
    },
  },
});