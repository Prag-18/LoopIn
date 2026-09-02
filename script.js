document.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector(".request-form");
  const list = document.querySelector(".match-list");
  const authForm = document.querySelector("[data-auth-form]");

  if (form) setUpRequestForm(form);
  if (list) setUpListings(list);
  if (authForm) setUpAuthForm(authForm);
});

function setUpRequestForm(form) {
  const start = document.querySelector("#date-start");
  const end = document.querySelector("#date-end");
  const today = new Date().toISOString().slice(0, 10);

  start.min = today;
  end.min = today;
  start.addEventListener("change", () => { end.min = start.value || today; });

  form.addEventListener("submit", (event) => {
    event.preventDefault();
    if (end.value < start.value) {
      end.setCustomValidity("Choose an end date after the start date.");
      end.reportValidity();
      return;
    }
    end.setCustomValidity("");

    const request = {
      id: Date.now(),
      item: form.elements["item-name"].value.trim(),
      start: start.value,
      end: end.value,
      degree: form.elements["trust-radius"].value
    };
    const requests = readRequests();
    requests.push(request);
    localStorage.setItem("loopin-requests", JSON.stringify(requests));
    window.location.assign("listings.html");
  });
}

function setUpListings(list) {
  readRequests().reverse().forEach((request) => list.prepend(createRequestCard(request)));
  document.querySelector(".badge-count").textContent = `${list.children.length} nearby`;

  list.addEventListener("click", ({ target }) => {
    if (target.matches(".withdraw-btn")) {
      withdrawRequest(target.closest(".match-card"), list);
      return;
    }

    if (!target.matches(".secondary-btn") || target.disabled) return;
    target.textContent = "Request sent";
    target.disabled = true;
  });
}

function readRequests() {
  try {
    const requests = JSON.parse(localStorage.getItem("loopin-requests") || "[]");
    return Array.isArray(requests) ? requests : [];
  } catch {
    return [];
  }
}

function createRequestCard(request) {
  const card = document.createElement("article");
  const connection = {
    1: ["1st degree", "badge-1st", "Your friends and classmates"],
    2: ["2nd degree", "badge-2nd", "Friends of friends"],
    3: ["Campus-wide", "badge-3rd", "Visible across campus"]
  }[request.degree] || ["Campus-wide", "badge-3rd", "Visible across campus"];

  card.className = `match-card degree-${request.degree}`;
  card.dataset.requestId = request.id;
  card.innerHTML = `<div class="trust-badge ${connection[1]}">${connection[0]}</div><div class="card-content"><div class="item-info"><h3></h3><p class="owner-meta">Pragati is looking to borrow this</p><div class="trust-proof"><span aria-hidden="true">◌</span> ${connection[2]} · ${formatDate(request.start)}–${formatDate(request.end)}</div></div><div class="action-block"><span class="price">Have one?</span><div class="request-actions"><button class="secondary-btn" type="button">Offer yours</button><button class="withdraw-btn" type="button">Withdraw request</button></div></div></div>`;
  card.querySelector("h3").textContent = request.item;
  return card;
}

function formatDate(value) {
  return new Intl.DateTimeFormat("en", { month: "short", day: "numeric" }).format(new Date(`${value}T00:00:00`));
}

function setUpAuthForm(form) {
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    const details = Object.fromEntries(new FormData(form));

    if (form.dataset.authForm === "signup") {
      localStorage.setItem("loopin-profile", JSON.stringify({ name: details.name, email: details.email }));
    }
    window.location.assign("index.html");
  });
}

function withdrawRequest(card, list) {
  const requestId = Number(card.dataset.requestId);
  const requests = readRequests().filter((request) => request.id !== requestId);
  localStorage.setItem("loopin-requests", JSON.stringify(requests));
  card.remove();
  document.querySelector(".badge-count").textContent = `${list.children.length} nearby`;
}
