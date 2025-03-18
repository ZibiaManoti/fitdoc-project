"use client";
import React from "react";

function MainComponent() {
  const { useState, useEffect } = React;
  const [activeSection, setActiveSection] = useState("categories");
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [customWorkout, setCustomWorkout] = useState([]);
  const [showWorkoutBuilder, setShowWorkoutBuilder] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const authStatus = localStorage.getItem("isLoggedIn");
    if (authStatus === "true") {
      setIsLoggedIn(true);
    }
  }, []);

  const handleLogout = () => {
    localStorage.setItem("isLoggedIn", "false");
    setIsLoggedIn(false);
  };

  const handleBack = () => {
    if (typeof window !== "undefined") {
      window.history.back();
    }
  };

  const workoutCategories = [
    { name: "Strength Training", icon: "fa-dumbbell" },
    { name: "Cardio", icon: "fa-running" },
    { name: "Flexibility", icon: "fa-child" },
    { name: "HIIT", icon: "fa-bolt" },
  ];

  const recommendedWorkouts = {
    beginner: [
      { name: "Basic Full Body", duration: "30 min", intensity: "Low" },
      { name: "Starter Cardio", duration: "20 min", intensity: "Low" },
    ],
    intermediate: [
      { name: "Advanced Core", duration: "45 min", intensity: "Medium" },
      { name: "Interval Running", duration: "30 min", intensity: "Medium" },
    ],
    advanced: [
      { name: "Power Lifting", duration: "60 min", intensity: "High" },
      { name: "CrossFit WOD", duration: "50 min", intensity: "High" },
    ],
  };

  const workoutHistory = [
    { date: "2025-01-15", name: "Morning Cardio", duration: "30 min" },
    { date: "2025-01-14", name: "Strength Training", duration: "45 min" },
    { date: "2025-01-12", name: "HIIT Session", duration: "25 min" },
  ];

  const achievements = [
    { name: "First Workout", icon: "fa-star", earned: true },
    { name: "10 Workouts", icon: "fa-medal", earned: true },
    { name: "Fitness Warrior", icon: "fa-trophy", earned: false },
  ];

  const renderCategories = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {workoutCategories.map((category) => (
        <div
          key={category.name}
          onClick={() => setSelectedCategory(category.name)}
          className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition-shadow cursor-pointer"
        >
          <div className="flex items-center justify-center mb-4">
            <i className={`fas ${category.icon} text-4xl text-orange-600`}></i>
          </div>
          <h3 className="text-xl font-bold text-center text-gray-800 font-roboto">
            {category.name}
          </h3>
        </div>
      ))}
    </div>
  );

  const renderRecommendedWorkouts = () => (
    <div className="space-y-6">
      {Object.entries(recommendedWorkouts).map(([level, workouts]) => (
        <div key={level} className="bg-white p-6 rounded-xl shadow-lg">
          <h3 className="text-xl font-bold mb-4 text-gray-800 font-roboto capitalize">
            {level} Level
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {workouts.map((workout) => (
              <div key={workout.name} className="border p-4 rounded-lg">
                <h4 className="font-semibold text-gray-800 font-roboto">
                  {workout.name}
                </h4>
                <p className="text-gray-600 font-roboto">
                  Duration: {workout.duration}
                </p>
                <p className="text-gray-600 font-roboto">
                  Intensity: {workout.intensity}
                </p>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );

  const renderWorkoutBuilder = () => (
    <div className="bg-white p-6 rounded-xl shadow-lg">
      <h3 className="text-xl font-bold mb-4 text-gray-800 font-roboto">
        Custom Workout Builder
      </h3>
      <div className="space-y-4">
        <div className="flex gap-4">
          <input
            type="text"
            name="exercise"
            placeholder="Add exercise"
            className="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
          <button className="bg-orange-600 text-white px-6 py-2 rounded-lg hover:bg-orange-700 transition-colors font-roboto">
            Add Exercise
          </button>
        </div>
        <div className="border rounded-lg p-4">
          <h4 className="font-semibold mb-2 text-gray-800 font-roboto">
            Current Workout
          </h4>
          <div className="space-y-2">
            {customWorkout.length === 0 ? (
              <p className="text-gray-500 font-roboto">
                No exercises added yet
              </p>
            ) : null}
          </div>
        </div>
      </div>
    </div>
  );

  const renderHistory = () => (
    <div className="bg-white p-6 rounded-xl shadow-lg">
      <h3 className="text-xl font-bold mb-4 text-gray-800 font-roboto">
        Workout History
      </h3>
      <div className="space-y-4">
        {workoutHistory.map((workout, index) => (
          <div
            key={index}
            className="flex justify-between items-center border-b pb-2"
          >
            <div>
              <h4 className="font-semibold text-gray-800 font-roboto">
                {workout.name}
              </h4>
              <p className="text-gray-600 font-roboto">{workout.date}</p>
            </div>
            <span className="text-gray-600 font-roboto">
              {workout.duration}
            </span>
          </div>
        ))}
      </div>
    </div>
  );

  const renderAchievements = () => (
    <div className="bg-white p-6 rounded-xl shadow-lg">
      <h3 className="text-xl font-bold mb-4 text-gray-800 font-roboto">
        Achievements
      </h3>
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {achievements.map((achievement) => (
          <div
            key={achievement.name}
            className={`p-4 rounded-lg text-center ${
              achievement.earned ? "bg-orange-50" : "bg-gray-50"
            }`}
          >
            <i
              className={`fas ${achievement.icon} text-2xl ${
                achievement.earned ? "text-orange-600" : "text-gray-400"
              }`}
            ></i>
            <p
              className={`mt-2 font-roboto ${
                achievement.earned ? "text-gray-800" : "text-gray-500"
              }`}
            >
              {achievement.name}
            </p>
          </div>
        ))}
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center mb-8">
          <div className="flex items-center space-x-4">
            <button
              onClick={handleBack}
              className="text-gray-600 hover:text-gray-900"
            >
              <i className="fas fa-arrow-left text-xl"></i>
            </button>
            <h1 className="text-3xl font-bold text-gray-900 font-roboto">
              Workouts
            </h1>
          </div>
          {isLoggedIn && (
            <button
              onClick={handleLogout}
              className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-roboto"
            >
              Logout
            </button>
          )}
        </div>

        <div className="mb-6 flex overflow-x-auto space-x-4">
          {[
            "categories",
            "recommended",
            "builder",
            "history",
            "achievements",
          ].map((section) => (
            <button
              key={section}
              onClick={() => setActiveSection(section)}
              className={`px-6 py-2 rounded-lg font-roboto whitespace-nowrap ${
                activeSection === section
                  ? "bg-orange-600 text-white"
                  : "bg-white text-gray-600 hover:bg-gray-50"
              }`}
            >
              {section.charAt(0).toUpperCase() + section.slice(1)}
            </button>
          ))}
        </div>

        <div className="space-y-6">
          {activeSection === "categories" && renderCategories()}
          {activeSection === "recommended" && renderRecommendedWorkouts()}
          {activeSection === "builder" && renderWorkoutBuilder()}
          {activeSection === "history" && renderHistory()}
          {activeSection === "achievements" && renderAchievements()}
        </div>
      </div>
    </div>
  );
}

export default MainComponent;