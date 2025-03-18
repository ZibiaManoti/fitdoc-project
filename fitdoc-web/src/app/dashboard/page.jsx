"use client";
import React from "react";

function MainComponent() {
  const { useState } = React;

  const [user] = useState({
    name: "John Doe",
    weight: 75,
    height: 175,
    goalWeight: 70,
    profilePicture: "/profile-placeholder.jpg",
  });

  const [metrics] = useState({
    heartRate: 75,
    caloriesBurned: 320,
    stepsToday: 6500,
    waterIntake: 5,
    exerciseMinutes: 45,
  });

  const [activities] = useState([
    { time: "8:00 AM", activity: "Morning Run", duration: "30 min" },
    { time: "10:00 AM", activity: "Protein Shake", type: "nutrition" },
    { time: "2:00 PM", activity: "Gym Workout", duration: "45 min" },
    { time: "4:00 PM", activity: "Water 500ml", type: "hydration" },
  ]);

  const calculateBMI = (weight, height) => {
    const heightInMeters = height / 100;
    return (weight / (heightInMeters * heightInMeters)).toFixed(1);
  };

  const bmi = calculateBMI(user.weight, user.height);

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex">
              <div className="flex-shrink-0 flex items-center">
                <span className="text-2xl font-bold text-blue-600 font-roboto">
                  FitTrack Pro
                </span>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <button className="text-gray-500">
                <i className="fas fa-bell text-xl"></i>
              </button>
              <button className="text-gray-500">
                <i className="fas fa-cog text-xl"></i>
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <div className="flex flex-col items-center">
                <div className="w-32 h-32 rounded-full overflow-hidden mb-4">
                  <img
                    src={user.profilePicture}
                    alt="Profile picture of John Doe"
                    className="w-full h-full object-cover"
                  />
                </div>
                <h2 className="text-2xl font-bold text-gray-900 font-roboto">
                  {user.name}
                </h2>
                <div className="mt-4 w-full space-y-2">
                  <div className="flex justify-between">
                    <span className="text-gray-600 font-roboto">Weight</span>
                    <span className="font-semibold font-roboto">
                      {user.weight} kg
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 font-roboto">Height</span>
                    <span className="font-semibold font-roboto">
                      {user.height} cm
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 font-roboto">BMI</span>
                    <span className="font-semibold font-roboto">{bmi}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 font-roboto">
                      Goal Weight
                    </span>
                    <span className="font-semibold font-roboto">
                      {user.goalWeight} kg
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="lg:col-span-2 space-y-6">
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
              <div className="bg-white p-4 rounded-xl shadow-lg">
                <div className="flex items-center">
                  <div className="p-3 bg-red-100 rounded-full">
                    <i className="fas fa-heartbeat text-red-500 text-xl"></i>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm text-gray-500 font-roboto">
                      Heart Rate
                    </p>
                    <p className="text-xl font-semibold font-roboto">
                      {metrics.heartRate} bpm
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl shadow-lg">
                <div className="flex items-center">
                  <div className="p-3 bg-blue-100 rounded-full">
                    <i className="fas fa-shoe-prints text-blue-500 text-xl"></i>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm text-gray-500 font-roboto">Steps</p>
                    <p className="text-xl font-semibold font-roboto">
                      {metrics.stepsToday}
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl shadow-lg">
                <div className="flex items-center">
                  <div className="p-3 bg-green-100 rounded-full">
                    <i className="fas fa-fire text-green-500 text-xl"></i>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm text-gray-500 font-roboto">
                      Calories
                    </p>
                    <p className="text-xl font-semibold font-roboto">
                      {metrics.caloriesBurned}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-lg p-6">
              <h3 className="text-xl font-semibold mb-4 font-roboto">
                Today's Activity
              </h3>
              <div className="space-y-4">
                {activities.map((activity, index) => (
                  <div key={index} className="flex items-center">
                    <div className="flex-shrink-0 w-16 text-sm text-gray-500 font-roboto">
                      {activity.time}
                    </div>
                    <div className="ml-4 flex-1">
                      <p className="text-sm font-medium text-gray-900 font-roboto">
                        {activity.activity}
                      </p>
                      {activity.duration && (
                        <p className="text-sm text-gray-500 font-roboto">
                          Duration: {activity.duration}
                        </p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        <div className="mt-6 grid grid-cols-2 md:grid-cols-4 gap-4">
          <button className="bg-blue-600 text-white p-4 rounded-xl shadow-lg hover:bg-blue-700 transition-colors">
            <i className="fas fa-dumbbell mb-2 text-xl"></i>
            <p className="font-roboto">Start Workout</p>
          </button>
          <button className="bg-green-600 text-white p-4 rounded-xl shadow-lg hover:bg-green-700 transition-colors">
            <i className="fas fa-utensils mb-2 text-xl"></i>
            <p className="font-roboto">Log Meal</p>
          </button>
          <button className="bg-cyan-600 text-white p-4 rounded-xl shadow-lg hover:bg-cyan-700 transition-colors">
            <i className="fas fa-glass-water mb-2 text-xl"></i>
            <p className="font-roboto">Track Water</p>
          </button>
          <button className="bg-purple-600 text-white p-4 rounded-xl shadow-lg hover:bg-purple-700 transition-colors">
            <i className="fas fa-weight-scale mb-2 text-xl"></i>
            <p className="font-roboto">Record Weight</p>
          </button>
        </div>
      </main>
    </div>
  );
}

export default MainComponent;