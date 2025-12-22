export function responseWrapper(req, res, next) {
  // -------------------------
  // SUCCESS: 200 OK
  // -------------------------
  res.ok = function (data = null, metadata = null, message = "Success") {
    if (metadata !== null) {
      return res.status(200).json({ data, metadata });
    } else {
      return res.status(200).json({ data });
    }
  };

  // -------------------------
  // SUCCESS: 201 CREATED
  // -------------------------
  res.created = function (data = null, message = "Created") {
    return res.status(201).json({ data });
  };

  // -------------------------
  // ERROR HANDLER GENERIC
  // -------------------------
  res.error = function (message = "Error", details = [], status = 400) {
    return res.status(status).json({
      error: {
        message,
        details,
      },
    });
  };

  // -------------------------
  // ERROR 404: NOT FOUND
  // -------------------------
  res.notFound = function (message = "Not Found") {
    return res.status(404).json({
      success: false,
      message,
      errors: [],
    });
  };

  // -------------------------
  // NEXT
  // -------------------------
  next();
}
