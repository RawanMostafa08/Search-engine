import React from "react";

export default function Navbar() {
  return (
    <>
      <nav class="navbar navbar-expand-lg navbar-light bg-dark border-bottom border-secondary">
        <div
          class="container-fluid"
          data-toggle="tooltip"
          data-placement="top"
          title="Your Powerful Search Engine"
        >
          <a href="/" className="navbar-brand">
            <h1 className="gradient-text-inf">In-Query</h1>
          </a>
        </div>
      </nav>
    </>
  );
}
