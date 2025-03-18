"use client";
import React from "react";

function MainComponent() {
  const { useState } = React;

  const [meals, setMeals] = useState([
    { id: 1, type: "Breakfast", calories: 0, foods: [] },
    { id: 2, type: "Lunch", calories: 0, foods: [] },
    { id: 3, type: "Dinner", calories: 0, foods: [] },
    { id: 4, type: "Snacks", calories: 0, foods: [] },
  ]);

  const [nutritionGoals, setNutritionGoals] = useState({
    dailyCalories: 2000,
    protein: 150,
    carbs: 200,
    fats: 65,
    water: 8,
  });

  const [waterIntake, setWaterIntake] = useState(0);
  const [selectedMeal, setSelectedMeal] = useState(null);
  const [newFood, setNewFood] = useState({
    name: "",
    calories: "",
    portion: "",
  });

  const handleBack = () => {
    if (typeof window !== "undefined") {
      window.history.back();
    }
  };

  const addFood = (mealId) => {
    if (newFood.name && newFood.calories) {
      setMeals(
        meals.map((meal) => {
          if (meal.id === mealId) {
            return {
              ...meal,
              foods: [...meal.foods, { ...newFood, id: Date.now() }],
              calories: meal.calories + Number(newFood.calories),
            };
          }
          return meal;
        })
      );
      setNewFood({ name: "", calories: "", portion: "" });
    }
  };

  const removeFood = (mealId, foodId, calories) => {
    setMeals(
      meals.map((meal) => {
        if (meal.id === mealId) {
          return {
            ...meal,
            foods: meal.foods.filter((food) => food.id !== foodId),
            calories: meal.calories - calories,
          };
        }
        return meal;
      })
    );
  };

  const totalCalories = meals.reduce((sum, meal) => sum + meal.calories, 0);

  const mealRecommendations = {
    Breakfast: [
      "Oatmeal with berries",
      "Greek yogurt parfait",
      "Whole grain toast with eggs",
    ],
    Lunch: ["Grilled chicken salad", "Quinoa bowl", "Turkey wrap"],
    Dinner: [
      "Salmon with vegetables",
      "Lean steak with sweet potato",
      "Stir-fry tofu",
    ],
    Snacks: [
      "Apple with almond butter",
      "Carrot sticks with hummus",
      "Trail mix",
    ],
  };

  return (
    <div className="min-h-screen bg-gray-50 p-4">
      <div className="max-w-7xl mx-auto">
        <button
          onClick={handleBack}
          className="text-gray-600 hover:text-gray-900"
        >
          <i className="fas fa-arrow-left text-xl"></i>
        </button>
        <h1 className="text-3xl font-bold text-gray-900 mb-8 font-roboto">
          Nutrition Tracker
        </h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            {meals.map((meal) => (
              <div key={meal.id} className="bg-white rounded-xl shadow-lg p-6">
                <div className="flex justify-between items-center mb-4">
                  <h2 className="text-xl font-semibold text-gray-900 font-roboto">
                    {meal.type}
                  </h2>
                  <span className="text-orange-600 font-roboto">
                    {meal.calories} calories
                  </span>
                </div>

                <div className="space-y-4">
                  {meal.foods.map((food) => (
                    <div
                      key={food.id}
                      className="flex justify-between items-center bg-gray-50 p-3 rounded-lg"
                    >
                      <div className="font-roboto">
                        <span className="font-medium">{food.name}</span>
                        <span className="text-gray-600 text-sm ml-2">
                          ({food.portion})
                        </span>
                      </div>
                      <div className="flex items-center space-x-4">
                        <span className="text-gray-600 font-roboto">
                          {food.calories} cal
                        </span>
                        <button
                          onClick={() =>
                            removeFood(meal.id, food.id, Number(food.calories))
                          }
                          className="text-red-500 hover:text-red-700"
                        >
                          <i className="fas fa-trash-alt"></i>
                        </button>
                      </div>
                    </div>
                  ))}

                  <div className="flex space-x-2">
                    <input
                      type="text"
                      placeholder="Food name"
                      className="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                      value={newFood.name}
                      onChange={(e) =>
                        setNewFood({ ...newFood, name: e.target.value })
                      }
                    />
                    <input
                      type="number"
                      placeholder="Calories"
                      className="w-24 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                      value={newFood.calories}
                      onChange={(e) =>
                        setNewFood({ ...newFood, calories: e.target.value })
                      }
                    />
                    <input
                      type="text"
                      placeholder="Portion"
                      className="w-24 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                      value={newFood.portion}
                      onChange={(e) =>
                        setNewFood({ ...newFood, portion: e.target.value })
                      }
                    />
                    <button
                      onClick={() => addFood(meal.id)}
                      className="bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition-colors"
                    >
                      <i className="fas fa-plus"></i>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 font-roboto">
                Daily Summary
              </h2>
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-gray-600 font-roboto">Calories</span>
                  <span className="font-medium font-roboto">
                    {totalCalories} / {nutritionGoals.dailyCalories}
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div
                    className="bg-orange-600 h-2.5 rounded-full"
                    style={{
                      width: `${Math.min(
                        (totalCalories / nutritionGoals.dailyCalories) * 100,
                        100
                      )}%`,
                    }}
                  ></div>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 font-roboto">
                Water Intake
              </h2>
              <div className="flex items-center space-x-4 mb-4">
                <button
                  onClick={() => setWaterIntake(Math.max(0, waterIntake - 1))}
                  className="bg-blue-100 text-orange-600 p-2 rounded-lg hover:bg-orange-200"
                >
                  <i className="fas fa-minus"></i>
                </button>
                <div className="flex-1 text-center">
                  <span className="text-3xl font-bold text-orange-600 font-roboto">
                    {waterIntake}
                  </span>
                  <span className="text-gray-600 ml-2 font-roboto">
                    / {nutritionGoals.water} glasses
                  </span>
                </div>
                <button
                  onClick={() => setWaterIntake(waterIntake + 1)}
                  className="bg-orange-100 text-orange-600 p-2 rounded-lg hover:bg-orange-200"
                >
                  <i className="fas fa-plus"></i>
                </button>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2.5">
                <div
                  className="bg-orange-600 h-2.5 rounded-full"
                  style={{
                    width: `${Math.min(
                      (waterIntake / nutritionGoals.water) * 100,
                      100
                    )}%`,
                  }}
                ></div>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 font-roboto">
                Meal Recommendations
              </h2>
              <div className="space-y-4">
                {Object.entries(mealRecommendations).map(
                  ([mealType, foods]) => (
                    <div key={mealType}>
                      <h3 className="font-medium text-gray-900 mb-2 font-roboto">
                        {mealType}
                      </h3>
                      <ul className="list-disc pl-5 text-gray-600 space-y-1">
                        {foods.map((food, index) => (
                          <li key={index} className="font-roboto">
                            {food}
                          </li>
                        ))}
                      </ul>
                    </div>
                  )
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default MainComponent;