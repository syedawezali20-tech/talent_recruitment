"use client";

import React, { useState } from "react";

interface VirtualKeyboardProps {
  onKeyPress: (key: string) => void;
  onBackspace: () => void;
  onClear: () => void;
  onEnter: () => void;
}

export default function VirtualKeyboard({
  onKeyPress,
  onBackspace,
  onClear,
  onEnter,
}: VirtualKeyboardProps) {
  const [isShiftOn, setIsShiftOn] = useState(false);

  const letterRows = [
    ["q", "w", "e", "r", "t", "y", "u", "i", "o", "p"],
    ["a", "s", "d", "f", "g", "h", "j", "k", "l"],
    ["z", "x", "c", "v", "b", "n", "m"],
  ];

  const numberRow = [
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    "7",
    "8",
    "9",
    "0",
  ];

  const specialCharacters = [
    "@",
    "$",
    "!",
    "%",
    "*",
    "?",
    "&",
  ];

  const handleLetterPress = (letter: string) => {
    const key = isShiftOn
      ? letter.toUpperCase()
      : letter;

    onKeyPress(key);

    // Shift behaves like a normal keyboard:
    // after typing one capital letter, turn Shift off.
    if (isShiftOn) {
      setIsShiftOn(false);
    }
  };

  return (
    <div className="mt-4 rounded-xl border border-slate-300 bg-slate-100 p-4 shadow-sm">

      {/* ======================================================
          TITLE
      ====================================================== */}

      <div className="mb-3 flex items-center justify-between">
        <div className="text-sm font-semibold text-slate-700">
          Virtual Keyboard
        </div>

        <div className="text-xs text-slate-500">
          {isShiftOn
            ? "Uppercase ON"
            : "Lowercase"}
        </div>
      </div>

      <div className="space-y-2">

        {/* ======================================================
            NUMBERS
        ====================================================== */}

        <div className="flex flex-wrap justify-center gap-2">
          {numberRow.map((key) => (
            <button
              key={key}
              type="button"
              onClick={() => onKeyPress(key)}
              className="min-w-[42px] rounded-lg border border-slate-300 bg-white px-3 py-2 font-medium text-slate-700 shadow-sm transition hover:bg-slate-200 active:scale-95 cursor-pointer"
            >
              {key}
            </button>
          ))}
        </div>

        {/* ======================================================
            ROW 1
        ====================================================== */}

        <div className="flex flex-wrap justify-center gap-2">
          {letterRows[0].map((letter) => (
            <button
              key={letter}
              type="button"
              onClick={() =>
                handleLetterPress(letter)
              }
              className="min-w-[42px] rounded-lg border border-slate-300 bg-white px-3 py-2 font-medium text-slate-700 shadow-sm transition hover:bg-slate-200 active:scale-95 cursor-pointer"
            >
              {isShiftOn
                ? letter.toUpperCase()
                : letter}
            </button>
          ))}
        </div>

        {/* ======================================================
            ROW 2
        ====================================================== */}

        <div className="flex flex-wrap justify-center gap-2">
          {letterRows[1].map((letter) => (
            <button
              key={letter}
              type="button"
              onClick={() =>
                handleLetterPress(letter)
              }
              className="min-w-[42px] rounded-lg border border-slate-300 bg-white px-3 py-2 font-medium text-slate-700 shadow-sm transition hover:bg-slate-200 active:scale-95 cursor-pointer"
            >
              {isShiftOn
                ? letter.toUpperCase()
                : letter}
            </button>
          ))}
        </div>

        {/* ======================================================
            ROW 3
        ====================================================== */}

        <div className="flex flex-wrap justify-center gap-2">
          {letterRows[2].map((letter) => (
            <button
              key={letter}
              type="button"
              onClick={() =>
                handleLetterPress(letter)
              }
              className="min-w-[42px] rounded-lg border border-slate-300 bg-white px-3 py-2 font-medium text-slate-700 shadow-sm transition hover:bg-slate-200 active:scale-95 cursor-pointer"
            >
              {isShiftOn
                ? letter.toUpperCase()
                : letter}
            </button>
          ))}
        </div>

        {/* ======================================================
            SPECIAL CHARACTERS
        ====================================================== */}

        <div className="flex flex-wrap justify-center gap-2 pt-1">
          {specialCharacters.map((key) => (
            <button
              key={key}
              type="button"
              onClick={() => onKeyPress(key)}
              className="min-w-[42px] rounded-lg border border-slate-300 bg-white px-3 py-2 font-medium text-slate-700 shadow-sm transition hover:bg-slate-200 active:scale-95 cursor-pointer"
            >
              {key}
            </button>
          ))}
        </div>

        {/* ======================================================
            CONTROL BUTTONS
        ====================================================== */}

        <div className="flex flex-wrap justify-center gap-2 pt-2">

          {/* SHIFT */}
          <button
            type="button"
            onClick={() =>
              setIsShiftOn((current) => !current)
            }
            className={`rounded-lg border px-5 py-2 font-semibold shadow-sm transition active:scale-95 cursor-pointer ${
              isShiftOn
                ? "border-blue-500 bg-blue-600 text-white hover:bg-blue-700"
                : "border-slate-300 bg-white text-slate-700 hover:bg-slate-200"
            }`}
          >
            ⇧ Shift
          </button>

          {/* BACKSPACE */}
          <button
            type="button"
            onClick={onBackspace}
            className="rounded-lg border border-slate-300 bg-white px-4 py-2 font-medium text-slate-700 shadow-sm transition hover:bg-slate-200 active:scale-95 cursor-pointer"
          >
            ← Backspace
          </button>

          {/* CLEAR */}
          <button
            type="button"
            onClick={onClear}
            className="rounded-lg border border-red-300 bg-red-50 px-4 py-2 font-medium text-red-700 shadow-sm transition hover:bg-red-100 active:scale-95 cursor-pointer"
          >
            Clear
          </button>

          {/* ENTER */}
          <button
            type="button"
            onClick={onEnter}
            className="rounded-lg border border-blue-300 bg-blue-50 px-6 py-2 font-medium text-blue-700 shadow-sm transition hover:bg-blue-100 active:scale-95 cursor-pointer"
          >
            Enter
          </button>

        </div>
      </div>
    </div>
  );
}