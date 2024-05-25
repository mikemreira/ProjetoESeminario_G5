import { Outlet, Route, RouterProvider, createBrowserRouter, createRoutesFromElements } from 'react-router-dom'
import SignUp from './user/SignUp.jsx'
import Home from './Home'
import LogIn from './user/LogIn.jsx'
import './App.css'
import Success from './user/Success.jsx'
import NavBar from './NavBar'
import Obras from "./obra/Obras.tsx"
import LogOut from "./user/LogOut.tsx"
import {AuthnContainer} from "./context/Authn.tsx";
import Profile from "./user/Profile.tsx";
import AddObra from "./obra/AddObra.tsx";

const AppLayout = () => {
  return (
  <>
    <AuthnContainer>
      <NavBar/>
      <Outlet/>
    </AuthnContainer>
  </>
  )
}

const router = createBrowserRouter([
  {
    "path": "/",
    "element": <AppLayout/>,
    "children": [
      {
          "path": "/",
          "element": <Home/>,
      },
      {
          "path": "/login",
          "element": <LogIn />
      },
      {
        "path": "/logout",
        "element": <LogOut />
      },
      {
        "path": "/signup",
        "element": <SignUp/>
      },
      {
        "path": "/success",
        "element": <Success/>
      },
      {
        "path": "/obras",
        "element": <Obras/>
      },
      {
        "path": "/profile",
        "element": <Profile />
      },
      {
        "path": "/addObra",
        "element": <AddObra/>
      }
  ]}
])

function App() {

  return (
    
      <RouterProvider router={router}>
      
      </RouterProvider>

  )
}

export default App
