"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { registerCandidate } from "@/lib/api";

export default function RegisterPage() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [showPassword, setShowPassword] = useState(false);

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phone, setPhone] = useState("");
  const [skills, setSkills] = useState("");
  const [experience, setExperience] = useState("");
  const [resumeUrl, setResumeUrl] = useState("");

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  // ============================================================
  // PASSWORD REQUIREMENTS
  // ============================================================

  const passwordRequirements = {
    minLength: password.length >= 12 && password.length <= 64,
    uppercase: /[A-Z]/.test(password),
    lowercase: /[a-z]/.test(password),
    number: /[0-9]/.test(password),
    special: /[@$!%*?&]/.test(password),
  };

  const isPasswordValid =
    passwordRequirements.minLength &&
    passwordRequirements.uppercase &&
    passwordRequirements.lowercase &&
    passwordRequirements.number &&
    passwordRequirements.special;

  // ============================================================
  // GENERATE STRONG PASSWORD
  // ============================================================

  function generateStrongPassword() {
    const uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const lowercase = "abcdefghijklmnopqrstuvwxyz";
    const numbers = "0123456789";
    const special = "@$!%*?&";

    const allCharacters =
      uppercase +
      lowercase +
      numbers +
      special;

    function getRandomCharacter(characters: string) {
      const randomValues = new Uint32Array(1);
      crypto.getRandomValues(randomValues);

      return characters[
        randomValues[0] % characters.length
      ];
    }

    // Start with one character from every required category
    let generatedPassword =
      getRandomCharacter(uppercase) +
      getRandomCharacter(lowercase) +
      getRandomCharacter(numbers) +
      getRandomCharacter(special);

    // Add additional characters until password reaches 16 characters
    while (generatedPassword.length < 16) {
      generatedPassword += getRandomCharacter(allCharacters);
    }

    // Securely shuffle the password
    const passwordCharacters =
      generatedPassword.split("");

    const randomValues =
      new Uint32Array(passwordCharacters.length);

    crypto.getRandomValues(randomValues);

    for (
      let i = passwordCharacters.length - 1;
      i > 0;
      i--
    ) {
      const j = randomValues[i] % (i + 1);

      [
        passwordCharacters[i],
        passwordCharacters[j],
      ] = [
        passwordCharacters[j],
        passwordCharacters[i],
      ];
    }

    const finalPassword =
      passwordCharacters.join("");

    setPassword(finalPassword);

    setError("");
    setMessage("");
  }

  // ============================================================
  // REGISTRATION
  // ============================================================

  async function handleRegister(
    event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    setMessage("");
    setError("");

    // Validate password before sending request
    if (!isPasswordValid) {
      setError(
        "Password must be 12–64 characters and contain at least one uppercase letter, one lowercase letter, one number, and one special character."
      );

      return;
    }

    setLoading(true);

    try {
      const result = await registerCandidate({
        username: username,
        email: email,
        password: password,
        firstName: firstName,
        lastName: lastName,
        phone: phone,
        skills: skills,
        experience: Number(experience),
        resumeUrl: resumeUrl,
      });

      if (!result.success) {
        setError(
          result.message || "Registration failed"
        );

        return;
      }

      setMessage(
        "Registration successful! Redirecting to login..."
      );

      setTimeout(() => {
        router.push("/");
      }, 1500);
    } catch (error) {
      console.error(error);

      setError(
        error instanceof Error
          ? error.message
          : "Registration failed"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="min-h-screen bg-slate-100 flex items-center justify-center px-4 py-10">
      <div className="w-full max-w-2xl">

        {/* ======================================================
            HEADER
        ====================================================== */}

        <div className="text-center mb-8">

          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-blue-600 text-white text-2xl font-bold shadow-lg">
            TR
          </div>

          <h1 className="mt-5 text-3xl font-bold text-slate-900">
            Candidate Registration
          </h1>

          <p className="mt-2 text-slate-500">
            Create your account and candidate profile
          </p>

        </div>

        {/* ======================================================
            REGISTRATION CARD
        ====================================================== */}

        <div className="bg-white rounded-2xl shadow-xl p-8">

          <h2 className="text-2xl font-semibold text-slate-900">
            Create your account
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            Enter your details to register as a candidate.
          </p>

          <form
            onSubmit={handleRegister}
            className="mt-6 space-y-5"
          >

            {/* ==================================================
                USERNAME
            ================================================== */}

            <div>

              <label
                htmlFor="username"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Username
              </label>

              <input
                id="username"
                type="text"
                value={username}
                onChange={(event) =>
                  setUsername(event.target.value)
                }
                placeholder="Enter username"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                EMAIL
            ================================================== */}

            <div>

              <label
                htmlFor="email"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Email
              </label>

              <input
                id="email"
                type="email"
                value={email}
                onChange={(event) =>
                  setEmail(event.target.value)
                }
                placeholder="Enter email"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                PASSWORD
            ================================================== */}

            <div>

              <label
                htmlFor="password"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Password
              </label>

              {/* ==================================================
                  PASSWORD INPUT + SHOW/HIDE BUTTON
              ================================================== */}

              <div className="relative">

                <input
                  id="password"
                  type={
                    showPassword
                      ? "text"
                      : "password"
                  }
                  value={password}
                  onChange={(event) =>
                    setPassword(event.target.value)
                  }
                  placeholder="Enter password"
                  required
                  disabled={loading}
                  autoComplete="new-password"
                  className="w-full rounded-lg border border-slate-300 px-4 py-3 pr-12 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowPassword(!showPassword)
                  }
                  disabled={loading}
                  aria-label={
                    showPassword
                      ? "Hide password"
                      : "Show password"
                  }
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-lg text-slate-500 hover:text-slate-700 disabled:opacity-50"
                >
                  {showPassword ? "🙈" : "👁️"}
                </button>

              </div>

              {/* ==================================================
                  PASSWORD REQUIREMENTS
              ================================================== */}

              <div className="mt-3 rounded-lg bg-slate-50 border border-slate-200 p-4">

                <p className="text-sm font-medium text-slate-700 mb-3">
                  Password must contain:
                </p>

                <ul className="space-y-1.5 text-sm">

                  {/* Minimum / maximum length */}

                  <li
                    className={
                      passwordRequirements.minLength
                        ? "text-green-600"
                        : "text-slate-500"
                    }
                  >
                    {passwordRequirements.minLength
                      ? "✓"
                      : "○"}{" "}
                    12–64 characters
                  </li>

                  {/* Uppercase */}

                  <li
                    className={
                      passwordRequirements.uppercase
                        ? "text-green-600"
                        : "text-slate-500"
                    }
                  >
                    {passwordRequirements.uppercase
                      ? "✓"
                      : "○"}{" "}
                    One uppercase letter (A-Z)
                  </li>

                  {/* Lowercase */}

                  <li
                    className={
                      passwordRequirements.lowercase
                        ? "text-green-600"
                        : "text-slate-500"
                    }
                  >
                    {passwordRequirements.lowercase
                      ? "✓"
                      : "○"}{" "}
                    One lowercase letter (a-z)
                  </li>

                  {/* Number */}

                  <li
                    className={
                      passwordRequirements.number
                        ? "text-green-600"
                        : "text-slate-500"
                    }
                  >
                    {passwordRequirements.number
                      ? "✓"
                      : "○"}{" "}
                    One number (0-9)
                  </li>

                  {/* Special character */}

                  <li
                    className={
                      passwordRequirements.special
                        ? "text-green-600"
                        : "text-slate-500"
                    }
                  >
                    {passwordRequirements.special
                      ? "✓"
                      : "○"}{" "}
                    One special character (@$!%*?&)
                  </li>

                </ul>

              </div>

              {/* ==================================================
                  GENERATE PASSWORD BUTTON
              ================================================== */}

              <button
                type="button"
                onClick={generateStrongPassword}
                disabled={loading}
                className="mt-3 w-full rounded-lg border border-blue-300 bg-blue-50 px-4 py-2.5 text-sm font-semibold text-blue-700 transition hover:bg-blue-100 disabled:cursor-not-allowed disabled:opacity-60"
              >
                🔑 Generate Strong Password
              </button>

              {/* ==================================================
                  PASSWORD VALID MESSAGE
              ================================================== */}

              {password && isPasswordValid && (
                <p className="mt-2 text-sm font-medium text-green-600">
                  ✓ Your password meets all requirements.
                </p>
              )}

            </div>

            {/* ==================================================
                FIRST NAME
            ================================================== */}

            <div>

              <label
                htmlFor="firstName"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                First Name
              </label>

              <input
                id="firstName"
                type="text"
                value={firstName}
                onChange={(event) =>
                  setFirstName(event.target.value)
                }
                placeholder="Enter first name"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                LAST NAME
            ================================================== */}

            <div>

              <label
                htmlFor="lastName"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Last Name
              </label>

              <input
                id="lastName"
                type="text"
                value={lastName}
                onChange={(event) =>
                  setLastName(event.target.value)
                }
                placeholder="Enter last name"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                PHONE
            ================================================== */}

            <div>

              <label
                htmlFor="phone"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Phone
              </label>

              <input
                id="phone"
                type="tel"
                value={phone}
                onChange={(event) =>
                  setPhone(event.target.value)
                }
                placeholder="Enter phone number"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                SKILLS
            ================================================== */}

            <div>

              <label
                htmlFor="skills"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Skills
              </label>

              <textarea
                id="skills"
                value={skills}
                onChange={(event) =>
                  setSkills(event.target.value)
                }
                placeholder="Example: Java, Spring Boot, PostgreSQL"
                rows={3}
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                EXPERIENCE
            ================================================== */}

            <div>

              <label
                htmlFor="experience"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Experience (Years)
              </label>

              <input
                id="experience"
                type="number"
                min="0"
                value={experience}
                onChange={(event) =>
                  setExperience(event.target.value)
                }
                placeholder="Enter years of experience"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                RESUME URL
            ================================================== */}

            <div>

              <label
                htmlFor="resumeUrl"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Resume URL
              </label>

              <input
                id="resumeUrl"
                type="url"
                value={resumeUrl}
                onChange={(event) =>
                  setResumeUrl(event.target.value)
                }
                placeholder="https://example.com/resume"
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
              />

            </div>

            {/* ==================================================
                ERROR
            ================================================== */}

            {error && (
              <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            {/* ==================================================
                SUCCESS
            ================================================== */}

            {message && (
              <div className="rounded-lg bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700">
                {message}
              </div>
            )}

            {/* ==================================================
                REGISTER BUTTON
            ================================================== */}

            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading
                ? "Creating account..."
                : "Create Candidate Account"}
            </button>

            {/* ==================================================
                BACK TO LOGIN
            ================================================== */}

            <button
              type="button"
              onClick={() => router.push("/")}
              disabled={loading}
              className="w-full rounded-lg border border-slate-300 px-4 py-3 font-semibold text-slate-700 transition hover:bg-slate-50 disabled:opacity-60"
            >
              Back to Sign In
            </button>

          </form>

        </div>

        {/* ======================================================
            FOOTER
        ====================================================== */}

        <p className="mt-6 text-center text-xs text-slate-400">
          Talent Recruitment Management System
        </p>

      </div>
    </main>
  );
}