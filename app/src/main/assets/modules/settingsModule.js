function handleStorageChangeLang(event) {
  if (event.key === "AppLanguageCode") {
    setTimeout(() => {
      applyTranslations(localStorage.getItem("AppLanguageCode"));

      const AppLanguageText = localStorage.getItem("AppLanguage");
      if (AppLanguageText) {
        document.getElementById("selectedAppLang_text").innerHTML =
          AppLanguageText;
      } else {
        document.getElementById("selectedAppLang_text").innerHTML = "English";
      }
    }, 200);
  }
}

window.addEventListener("storage", handleStorageChangeLang);

const user1 = document.getElementById("headUser-1");

function changeColorOnScroll1() {
  var container = document.getElementById("headUser-1");
  var scrollY = container.scrollTop;

  if (scrollY >= 50) {
    document.getElementById("head-1").style.backgroundColor =
      "var(--Surface-Container)";
    document.getElementById("head-1").style.transition = "";

    document.getElementById("headUser-1").style.scrollSnapType = "unset";
    container.removeEventListener("scroll", changeColorOnScroll1);
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_Scrolled`
    );
  } else {
  }
}

if (user1) {
  document
    .getElementById("headUser-1")
    .addEventListener("scroll", changeColorOnScroll1);
}

function changeColorOnScroll1A() {
  var container = document.getElementById("headUser-1");
  var scrollY = container.scrollTop;

  if (scrollY <= 1) {
    document.getElementById("head-1").style.backgroundColor = "";
    document.getElementById("head-1").style.transition =
      "background-color 0.2s ease-in";

    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
    );

    container.addEventListener("scroll", changeColorOnScroll1);
    if (scrollY <= 20);
    {
      document.getElementById("headUser-1").style.scrollSnapType = "";
    }
  } else {
  }
}

if (user1) {
  document
    .getElementById("headUser-1")
    .addEventListener("scroll", changeColorOnScroll1A);
}

function sendThemeToAndroid(theme) {
  AndroidInterface.updateStatusBarColor(theme);
}

const selectTempUnit = document.getElementById("selectTempUnit");

selectTempUnit.addEventListener("input", () => {
  localStorage.setItem("SelectedTempUnit", selectTempUnit.value);
});

if (!localStorage.getItem("SelectedTempUnit")) {
  document.querySelector(`[value="celsius"]`).selected = true;
}

const getSelectedTempUnit = localStorage.getItem("SelectedTempUnit");

if (getSelectedTempUnit) {
  document.querySelector(`[value="${getSelectedTempUnit}"]`).selected = true;
}

const selectWindUnit = document.getElementById("selectWindUnit");

selectWindUnit.addEventListener("input", () => {
  localStorage.setItem("SelectedWindUnit", selectWindUnit.value);
});

if (!localStorage.getItem("SelectedWindUnit")) {
  document.querySelector(`[value="kilometer"]`).selected = true;
}

const getSelectedWindUnit = localStorage.getItem("SelectedWindUnit");

if (getSelectedWindUnit) {
  document.querySelector(`[value="${getSelectedWindUnit}"]`).selected = true;
}

const selectVisibilityUnit = document.getElementById("selectVisibilityUnit");

selectVisibilityUnit.addEventListener("input", () => {
  localStorage.setItem("selectedVisibilityUnit", selectVisibilityUnit.value);
});

if (!localStorage.getItem("selectedVisibilityUnit")) {
  document.querySelector(`[value="km"]`).selected = true;
}

const getSelectedVisibilityUnit = localStorage.getItem(
  "selectedVisibilityUnit"
);

if (getSelectedVisibilityUnit) {
  document.querySelector(
    `[value="${getSelectedVisibilityUnit}"]`
  ).selected = true;
}

const selectPrecipitationUnit = document.getElementById(
  "selectPrecipitationUnit"
);

selectPrecipitationUnit.addEventListener("input", () => {
  localStorage.setItem(
    "selectedPrecipitationUnit",
    selectPrecipitationUnit.value
  );
});

if (!localStorage.getItem("selectedPrecipitationUnit")) {
  document.querySelector(`[value="mm"]`).selected = true;
}

const getSelectedPrecipitationUnit = localStorage.getItem(
  "selectedPrecipitationUnit"
);

if (localStorage.getItem("selectedPrecipitationUnit")) {
  document.querySelector(
    `[value="${getSelectedPrecipitationUnit}"]`
  ).selected = true;
}

// -----------------------

const selectPressureUnit = document.getElementById("selectPressureUnit");

selectPressureUnit.addEventListener("input", () => {
  localStorage.setItem("selectedPressureUnit", selectPressureUnit.value);
});

if (!localStorage.getItem("selectedPressureUnit")) {
  document.querySelector(`[value="hPa"]`).selected = true;
}

const getSelectedPressureUnit = localStorage.getItem("selectedPressureUnit");

if (localStorage.getItem("selectedPressureUnit")) {
  document.querySelector(
    `[value="${getSelectedPressureUnit}"]`
  ).selected = true;
}

// time format

const selectTimeformat = document.getElementById("selectTimeformat");

selectTimeformat.addEventListener("input", () => {
  localStorage.setItem("selectedTimeMode", selectTimeformat.value);
});

if (!localStorage.getItem("selectedTimeMode")) {
  document.querySelector(`[value="12 hour"]`).selected = true;
}

const getSelectedTimeFormat = localStorage.getItem("selectedTimeMode");

if (localStorage.getItem("selectedTimeMode")) {
  document.querySelector(`[value="${getSelectedTimeFormat}"]`).selected = true;
}

// background animations

const useAnimationBackgroundSwitch = document.getElementById(
  "useAnimationBackground"
);

useAnimationBackgroundSwitch.addEventListener("change", () => {
  if (useAnimationBackgroundSwitch.selected) {
    localStorage.setItem("UseBackgroundAnimations", "true");
  } else {
    localStorage.setItem("UseBackgroundAnimations", "false");
  }
});

const getUseBackgroundAnimations = localStorage.getItem(
  "UseBackgroundAnimations"
);

if (getUseBackgroundAnimations) {
  if (getUseBackgroundAnimations === "true") {
    useAnimationBackgroundSwitch.selected = true;
  } else {
    useAnimationBackgroundSwitch.selected = false;
  }
} else {
  localStorage.setItem("UseBackgroundAnimations", "true");
}

const AppLanguageText = localStorage.getItem("AppLanguage");
if (AppLanguageText) {
  document.getElementById("selectedAppLang_text").innerHTML = AppLanguageText;
} else {
  document.getElementById("selectedAppLang_text").innerHTML = "English";
}

// ----------------------------------------------------------------------------------

// ---------------------------------------------------

const HideNewUpdateToastSwitch = document.getElementById(
  "HideNewUpdateToastSwitch"
);

HideNewUpdateToastSwitch.addEventListener("change", () => {
  localStorage.setItem("HideNewUpdateToast", HideNewUpdateToastSwitch.selected);
});

const HideNewUpdateToast = localStorage.getItem("HideNewUpdateToast");

if (HideNewUpdateToast === "true") {
  HideNewUpdateToastSwitch.selected = true;
} else {
  HideNewUpdateToastSwitch.selected = false;
}
// -------------------------------------

document.getElementById("toggle_noti").addEventListener("change", () => {
  if (document.getElementById("toggle_noti").selected) {
    setTimeout(() => {
      sendThemeToAndroid("ReqNotification");
    }, 1000);
    localStorage.setItem("UseNotification", "true");
  } else {
    localStorage.setItem("UseNotification", "false");
  }
});

if (
  localStorage.getItem("UseNotification") &&
  localStorage.getItem("UseNotification") === "true"
) {
  document.getElementById("toggle_noti").selected = true;
} else {
  document.getElementById("toggle_noti").selected = false;
}

function handlePermissionDenied() {
  localStorage.setItem("UseNotification", "false");
  document.getElementById("toggle_noti").click();
}

document.getElementById("useBarChart").addEventListener("change", () => {
  if (document.getElementById("useBarChart").selected) {
    localStorage.setItem("useBarChart", "true");
  } else {
    localStorage.setItem("useBarChart", "false");
  }
});

if (
  localStorage.getItem("useBarChart") &&
  localStorage.getItem("useBarChart") === "true"
) {
  document.getElementById("useBarChart").selected = true;
} else {
  document.getElementById("useBarChart").selected = false;
}

function ChangeDefaultLocationName() {
  const checkName = JSON.parse(localStorage.getItem("DefaultLocation"));

  if (checkName.name === "CurrentDeviceLocation") {
    document.getElementById("homeLocationSelectedText").innerHTML =
      "Your device location";
  } else if (checkName) {
    document.getElementById("homeLocationSelectedText").innerHTML =
      checkName.name;
  } else {
    document.getElementById("homeLocationSelectedText").innerHTML =
      "Your device location";
  }
}

function handleStorageChangeDefaultLocation(event) {
  if (event.key === "DefaultLocation") {
    setTimeout(() => {
      ChangeDefaultLocationName();
    }, 500);
  }
}

window.addEventListener("storage", handleStorageChangeDefaultLocation);

document.addEventListener("DOMContentLoaded", () => {
  ChangeDefaultLocationName();
});

function openMainProviderDialog() {
  document.getElementById("chooseMainProviderDialog").show();
  window.history.pushState({ MainProviderDialogOpen: true }, "");

  document
    .querySelector(
      `md-radio[value="${
        localStorage.getItem("selectedMainWeatherProvider") || "open-meteo"
      }"]`
    )
    .setAttribute("checked", "true");

  if (document.getElementById("headUser-1").scrollTop >= 50) {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogScrolled`
    );
  } else {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogNotScrolled`
    );
  }
}

window.addEventListener("popstate", function (event) {
  if (document.getElementById("chooseMainProviderDialog").open) {
    document.getElementById("chooseMainProviderDialog").close();
  }
});

document
  .getElementById("chooseMainProviderDialog")
  .addEventListener("cancel", () => {
    document
      .getElementById("chooseMainProviderDialog")
      .addEventListener("closed", () => {
        window.history.back();
      });
  });

document
  .getElementById("chooseMainProviderDialog")
  .addEventListener("close", () => {
    if (document.getElementById("headUser-1").scrollTop >= 50) {
      sendThemeToAndroid(
        `${document.documentElement.getAttribute("Theme")}_Scrolled`
      );
    } else {
      sendThemeToAndroid(
        `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
      );
    }
  });

// ----------

function ChangeSelectedMainProviderName() {
  const checkName = localStorage.getItem("selectedMainWeatherProvider");

  if (checkName === "Met norway") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "Met Norway (Global)";
  } else if (
    localStorage.getItem("ApiForAccu") &&
    checkName === "Accuweather"
  ) {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "Accuweather (Global)";
  } else if (checkName === "meteoFrance") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "Météo-France (Europe, Global)";
  } else if (checkName === "dwdGermany") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "DWD (Europe, Global)";
  } else if (checkName === "noaaUS") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "NOAA (Americas, Global)";
  } else if (checkName === "ecmwf") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "ECMWF (Global)";
  } else if (checkName === "ukMetOffice") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "UK Met Office (Europe, Global)";
  } else if (checkName === "jmaJapan") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "JMA (Asia, Global)";
  } else if (checkName === "gemCanada") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "GEM (Americas, Global)";
  } else if (checkName === "bomAustralia") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "BOM (Oceania, Global)";
  } else if (checkName === "cmaChina") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "CMA (Asia, Global)";
  } else if (checkName === "knmiEurope") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "KNMI (Europe, Global)";
  } else if (checkName === "dmiEurope") {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "DMI (Europe, Global)";
  } else {
    document.getElementById("mainProviderSelectedText").innerHTML =
      "Open-Meteo (Global)";
  }
}

document.addEventListener("DOMContentLoaded", () => {
  ChangeSelectedMainProviderName();

  if (localStorage.getItem("UseNotification")) {
    setTimeout(() => {
      localStorage.removeItem("UseNotification");
      document.getElementById("toggle_noti").selected = false;
    }, 500);
  }

  document.getElementById("useTempAnimation").addEventListener("change", () => {
    localStorage.setItem(
      "useTempAnimation",
      document.getElementById("useTempAnimation").selected
    );
  });

  if (
    localStorage.getItem("useTempAnimation") &&
    localStorage.getItem("useTempAnimation") === "false"
  ) {
    document.getElementById("useTempAnimation").selected = false;
  } else {
    document.getElementById("useTempAnimation").selected = true;
  }
});

// ------------

function openAppFontDialog() {
  document.getElementById("chooseAPPFontDialog").show();
  window.history.pushState({ AppFontDialogOpen: true }, "");

  document
    .querySelector(
      `md-radio[value="${
        localStorage.getItem("SelectedAPPfont") || "product_sans"
      }"]`
    )
    .setAttribute("checked", "true");

  if (document.getElementById("headUser-1").scrollTop >= 50) {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogScrolled`
    );
  } else {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogNotScrolled`
    );
  }
}

window.addEventListener("popstate", function (event) {
  if (document.getElementById("chooseAPPFontDialog").open) {
    document.getElementById("chooseAPPFontDialog").close();
  }
});

document
  .getElementById("chooseAPPFontDialog")
  .addEventListener("cancel", () => {
    document
      .getElementById("chooseAPPFontDialog")
      .addEventListener("closed", () => {
        window.history.back();
      });
  });

document.getElementById("chooseAPPFontDialog").addEventListener("close", () => {
  if (document.getElementById("headUser-1").scrollTop >= 50) {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_Scrolled`
    );
  } else {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
    );
  }
});

document.getElementById("saveSelectedFont").addEventListener("click", () => {
  if (document.querySelector('md-radio[value="product_sans"]').checked) {
    localStorage.setItem("SelectedAPPfont", "product_sans");
    document.documentElement.setAttribute("sys-font", "");
  } else {
    localStorage.setItem("SelectedAPPfont", "roboto");
    document.documentElement.setAttribute("sys-font", "roboto");
  }
  window.history.back();
  applyFontChanges();
});

// ------------------

document.getElementById("useWeatherAlerts").addEventListener("change", () => {
  localStorage.setItem(
    "useWeatherAlerts",
    document.getElementById("useWeatherAlerts").selected
  );
});

if (
  localStorage.getItem("useWeatherAlerts") &&
  localStorage.getItem("useWeatherAlerts") === "false"
) {
  document.getElementById("useWeatherAlerts").selected = false;
} else {
  document.getElementById("useWeatherAlerts").selected = true;
}

document.getElementById("exportAppData").addEventListener("click", () => {
  const data = { ...localStorage };

  // Check if local storage has data
  if (Object.keys(data).length > 0) {
    // Convert data to JSON string
    const jsonData = JSON.stringify(data, null, 2);

    // Use Android interface to save the file
    Android.saveFile(jsonData);
  } else {
    // Show toast message if no data is found
    ShowSnackMessage.ShowSnack("No data found", "short");
  }
});

document.getElementById("importAppData").addEventListener("click", () => {
  Android.importFile();
});

function handleImportedData(importedData) {
  try {
    const data = JSON.parse(importedData);

    if (typeof data !== "object" || data === null) {
      throw new Error("Invalid data format");
    }

    localStorage.clear();

    setTimeout(() => {
      for (const [key, value] of Object.entries(data)) {
        localStorage.setItem(key, value);
      }
      ShowSnackMessage.ShowSnack("Data imported successfully!", "short");
    }, 1000);
    setTimeout(() => {
      Android.reloadTheApp();
    }, 1500);
  } catch (error) {
    ShowSnackMessage.ShowSnack(
      "Error parsing the imported JSON data. Please ensure it is valid and correctly formatted.",
      "long"
    );
    console.error(error);
  }
}

//  ----------------

document
  .getElementById("useFroggySummarySwitch")
  .addEventListener("change", () => {
    localStorage.setItem(
      "UseFrogSummary",
      document.getElementById("useFroggySummarySwitch").selected
    );
  });

if (
  localStorage.getItem("UseFrogSummary") &&
  localStorage.getItem("UseFrogSummary") === "true"
) {
  document.getElementById("useFroggySummarySwitch").selected = true;
} else {
  document.getElementById("useFroggySummarySwitch").selected = false;
}

//--------------------------

const colorItems = document.querySelectorAll(".color-item");

colorItems.forEach((item) => {
  item.addEventListener("click", () => {
    colorItems.forEach((i) => i.style.setProperty("--display-style", "none"));
    item.style.setProperty("--display-style", "block");
    document.documentElement.setAttribute(
      "Theme",
      item.getAttribute("theme-value")
    );
    setCustomTheme(item.getAttribute("theme-value"));
  });
});

async function setCustomTheme(item) {
  await customStorage.setItem("customTheme", item);

  setTimeout(() => {
        if (document.getElementById("headUser-1").scrollTop >= 50) {
          sendThemeToAndroid(
            `${document.documentElement.getAttribute("Theme")}_Scrolled`
          );
        } else {
          sendThemeToAndroid(
            `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
          );
        }
  }, 100);
}

async function LoadCustomTheme() {
  if (await customStorage.getItem("customTheme")) {
    document.documentElement.setAttribute(
      "Theme",
      await customStorage.getItem("customTheme")
    );
    document
      .querySelector(
        `[theme-value="${await customStorage.getItem("customTheme")}"]`
      )
      .style.setProperty("--display-style", "block");

  } else {
   document.documentElement.setAttribute(
     "Theme",
        "blue_material"
   );
     await customStorage.setItem("customTheme", "blue_material");

     document
       .querySelector(
         `[theme-value="blue_material"]`
       )
       .style.setProperty("--display-style", "block");
  }

}
document.addEventListener("DOMContentLoaded", async () => {
  if (!document.getElementById("useDynamicColors").selected) {
    LoadCustomTheme();

    setTimeout(async() => {
     if (await customStorage.getItem("customTheme")) {
            sendThemeToAndroid(
              `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
            );
       }
    }, 100);
  }
});

document.getElementById("useDynamicColors").addEventListener("change", () => {
  localStorage.setItem(
    "useDynamicColors",
    document.getElementById("useDynamicColors").selected
  );

  if (document.getElementById("useDynamicColors").selected) {
    document.querySelector(".custom_theme_items").classList.add("hidden");
    setTimeout(() => {
     sendThemeToAndroid('ReloadDynamicColors');
    }, 250);
  } else {
    document.querySelector(".custom_theme_items").classList.remove("hidden");

     setTimeout(() => {
           if (document.getElementById("headUser-1").scrollTop >= 50) {
             sendThemeToAndroid(
               `${document.documentElement.getAttribute("Theme")}_Scrolled`
             );
           } else {
             sendThemeToAndroid(
               `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
             );
           }


     }, 100);

    LoadCustomTheme();


  }
});

if (
  localStorage.getItem("useDynamicColors") &&
  localStorage.getItem("useDynamicColors") === "false"
) {
  document.querySelector(".custom_theme_items").classList.remove("hidden");
  document.getElementById("useDynamicColors").selected = false;
} else {
  document.querySelector(".custom_theme_items").classList.add("hidden");
  document.getElementById("useDynamicColors").selected = true;
}

// ------------------------------------------

function openAppFontSizeDialog() {
  document.getElementById("chooseAPPFontSizeDialog").show();
  window.history.pushState({ AppFontSizeDialogOpen: true }, "");

  document
    .querySelector(
      `md-radio[value="${
        localStorage.getItem("SelectedAPPfontSize") || "normal_fontSize"
      }"]`
    )
    .setAttribute("checked", "true");

  if (document.getElementById("headUser-1").scrollTop >= 50) {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogScrolled`
    );
  } else {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogNotScrolled`
    );
  }
}

window.addEventListener("popstate", function (event) {
  if (document.getElementById("chooseAPPFontSizeDialog").open) {
    document.getElementById("chooseAPPFontSizeDialog").close();
  }
});

document
  .getElementById("chooseAPPFontSizeDialog")
  .addEventListener("cancel", () => {
    document
      .getElementById("chooseAPPFontSizeDialog")
      .addEventListener("closed", () => {
        window.history.back();
      });
  });

document
  .getElementById("chooseAPPFontSizeDialog")
  .addEventListener("close", () => {
    if (document.getElementById("headUser-1").scrollTop >= 50) {
      sendThemeToAndroid(
        `${document.documentElement.getAttribute("Theme")}_Scrolled`
      );
    } else {
      sendThemeToAndroid(
        `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
      );
    }
  });

document
  .getElementById("saveSelectedFontSize")
  .addEventListener("click", () => {
    if (document.querySelector('md-radio[value="normal_fontSize"]').checked) {
      localStorage.setItem("SelectedAPPfontSize", "normal_fontSize");
      document.documentElement.setAttribute("sys-font-size", "");
    } else if (
      document.querySelector('md-radio[value="medium_fontSize"]').checked
    ) {
      localStorage.setItem("SelectedAPPfontSize", "medium_fontSize");
      document.documentElement.setAttribute("sys-font-size", "medium_fontSize");
    } else {
      localStorage.setItem("SelectedAPPfontSize", "large_fontSize");
      document.documentElement.setAttribute("sys-font-size", "large_fontSize");
    }
    window.history.back();
    applyFontChanges();
  });

    // ----------------

document.getElementById('ThresholdSlider').addEventListener('input', async() =>{
    await customStorage.setItem('ThresholdSwipeValue', document.getElementById('ThresholdSlider').value)
    document.getElementById('ThresholdValueText').innerHTML = document.getElementById('ThresholdSlider').value
})


async function loadThresholdSlider() {
    if(await customStorage.getItem('ThresholdSwipeValue')){
        document.getElementById('ThresholdSlider').value = await customStorage.getItem('ThresholdSwipeValue')
        document.getElementById('ThresholdValueText').innerHTML = await customStorage.getItem('ThresholdSwipeValue')
    }

    if(await customStorage.getItem('useGestureLocation') && await customStorage.getItem('useGestureLocation') === true){
        document.getElementById('useGestureLocation').selected = true
    } else{
        document.getElementById('useGestureLocation').selected = false
    }
}



document.addEventListener('DOMContentLoaded', ()=>{
    loadThresholdSlider()
})

// -------

document.getElementById('useGestureLocation').addEventListener('change', async () =>{
    await customStorage.setItem('useGestureLocation', document.getElementById('useGestureLocation').selected);
})

// -----------





function openSwipeThresholdDialog() {
  document.getElementById("chooseSwipeThresholdDialog").show();
  window.history.pushState({ AppSwipeThresholdDialogOpen: true }, "");

  if (document.getElementById("headUser-1").scrollTop >= 50) {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogScrolled`
    );
  } else {
    sendThemeToAndroid(
      `${document.documentElement.getAttribute("Theme")}_DialogNotScrolled`
    );
  }
}

window.addEventListener("popstate", function (event) {
  if (document.getElementById("chooseSwipeThresholdDialog").open) {
    document.getElementById("chooseSwipeThresholdDialog").close();
  }
});

document
  .getElementById("chooseSwipeThresholdDialog")
  .addEventListener("cancel", () => {
    document
      .getElementById("chooseSwipeThresholdDialog")
      .addEventListener("closed", () => {
        window.history.back();
      });
  });

document
  .getElementById("chooseSwipeThresholdDialog")
  .addEventListener("close", () => {
    if (document.getElementById("headUser-1").scrollTop >= 50) {
      sendThemeToAndroid(
        `${document.documentElement.getAttribute("Theme")}_Scrolled`
      );
    } else {
      sendThemeToAndroid(
        `${document.documentElement.getAttribute("Theme")}_ScrollFalse`
      );
    }
  });


    // aqi type

  const selectAQItype = document.getElementById("selectAQItype");

  selectAQItype.addEventListener("input", () => {
    localStorage.setItem("selectedAQItype", selectAQItype.value);
  });

  if (!localStorage.getItem("selectedAQItype")) {
    document.querySelector(`[value="us_aqi"]`).selected = true;
  }

  const getSelectedAQItype = localStorage.getItem("selectedAQItype");

  if (localStorage.getItem("selectedAQItype")) {
    document.querySelector(`[value="${getSelectedAQItype}"]`).selected = true;
  }