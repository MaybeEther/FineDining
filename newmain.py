def get_user_input():
    location = input("Pick 1, 2 or 3: \n[1] Prime\n[2] Alo\n[3] Japanese\n")
    flocation=""
    if location == "1":
        flocation = "1924-Prime"
    elif location == "2":
        flocation = "alo"
    elif location == "3":
        flocation = "kazoku"

    date = input("Enter the date you would like in the format YYYY-MM-DD: ")
    guestNum = input("How many guests will you be having? ")
    time = input("What time do you want to go at? ")
    username = input("what is your school username? ")
    psswrd = input("what is your account password? ")
    phone = input("whats your tellephone? ")
    atime = time.split(":")
    htime = int(atime[0]) + 12 if int(atime[0]) < 12 else int(atime[0])
    user_data = [flocation, date, guestNum, str(htime), atime[1], username, psswrd, phone]
    return user_data
def main():
    # Step 1: Get user input
    user_data = get_user_input()
    with open("user_data.txt", "w") as f:
        for item in user_data:
            f.write(item + "\n")

if __name__ == "__main__":
    main()