import axios from "axios";

export const api = axios.create({
  baseURL:
    import.meta.env.VITE_BASE_URL ??
    "http://10.40.12.77:8080/WS_EUREKABANK_RESTFUL_JAVA_GR01/",
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
    }

    return Promise.reject(error);
  },
);
