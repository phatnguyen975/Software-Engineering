import morgan from "morgan";

morgan.token("time", () => {
  return new Date().toISOString().replace("T", " ").split(".")[0];
});

const reset = "\x1b[0m";
const green = "\x1b[32m";
const yellow = "\x1b[33m";
const red = "\x1b[31m";
const blue = "\x1b[34m";

const customFormat = (tokens, req, res) => {
  const status = res.statusCode;
  const method = req.method;
  const url = req.url;
  const responseTime = tokens["response-time"](req, res);

  const methodColor = method === "GET" ? green : yellow;
  const statusColor = status >= 400 ? red : green;

  return [
    `${blue}[${tokens.time(req, res)}]${reset}`,
    `${methodColor}${method}${reset}`,
    `${statusColor}${status}${reset}`,
    `${url}`,
    `(${responseTime} ms)`
  ].join(" ");
};

const logger = morgan(customFormat);

export { logger };
