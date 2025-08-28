from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from datetime import datetime, timedelta
import time
def read_user_data():
    try:
        with open("user_data.txt", "r") as f:
            user_data = [line.strip() for line in f.readlines()]
            return user_data
    except FileNotFoundError:
        return []
def excicute(user):
    prowler = webdriver.Chrome()
    prowler.get("https://finedining.highpoint.edu/login")
    login_box1 = prowler.find_element(By.ID, 'login-username')
    login_box2 = prowler.find_element(By.ID, 'login-password')
    login_box1.send_keys(user[5])
    login_box2.send_keys(user[6])
    login_box2.send_keys(Keys.RETURN)  # login to fine dining
    prowler.refresh()
    prowler.get(
        f"https://finedining.highpoint.edu/{user[0]}/reservation?picker_step=2&location=2&date={user[1]}&guest={user[2]}&sdateTime={user[1]}+{user[3]}%3A{user[4]}")
    phone_box = WebDriverWait(prowler, 10).until(EC.presence_of_element_located((By.ID, 'telephone')))
    phone_box.send_keys(user[7])
    button = WebDriverWait(prowler, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR,'button.btn.btn-primary.btn-block.btn-lg')))
    button.click()
    prowler.refresh()
    print("sucess? ")
    prowler.close()
def main():
    user_data = read_user_data()
    given_date = datetime.strptime(user_data[1], '%Y-%m-%d')
    current_date = datetime.today()
    if given_date > current_date + timedelta(weeks=1):
        print("The given date is more than a week away from today.")
        target_date = ((given_date - timedelta(weeks=1)))
        target_date = target_date.replace(hour=0, minute=1, second=1)
        for i in range(0, 365):
            if current_date.hour >= 2:
                target_date += timedelta(days=1)
            time_difference = (target_date - current_date).total_seconds()
            if time_difference > 0:
                time.sleep(time_difference)
            else:
                print("The target date is in the past.")
            excicute(user_data)
    else:
        print("The given date is within a week from today.")
        excicute(user_data)
if __name__ == "__main__":
    main()