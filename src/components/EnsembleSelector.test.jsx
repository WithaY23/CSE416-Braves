import React from "react";
import { describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen } from "@testing-library/react";
import EnsembleSelector from "./EnsembleSelector.jsx";

describe("EnsembleSelector", () => {
  it("renders only three options for race-blind when maxOptions is 3", () => {
    const onSwitch = vi.fn();
    render(<EnsembleSelector ensembleType="rb" currEnsemble={1} switchEnsemble={onSwitch} maxOptions={3} />);

    fireEvent.click(screen.getByText("Race-Blind Ensemble 1"));

    expect(screen.getByText("Race-Blind Ensemble 2")).toBeInTheDocument();
    expect(screen.getByText("Race-Blind Ensemble 3")).toBeInTheDocument();
    expect(screen.queryByText("Race-Blind Ensemble 4")).not.toBeInTheDocument();
  });

  it("switches by numeric ensemble index", () => {
    const onSwitch = vi.fn();
    render(<EnsembleSelector ensembleType="vra" currEnsemble={1} switchEnsemble={onSwitch} maxOptions={4} />);

    fireEvent.click(screen.getByText("VRA Ensemble 1"));
    fireEvent.click(screen.getByText("VRA Ensemble 4"));

    expect(onSwitch).toHaveBeenCalledWith(4);
  });
});
