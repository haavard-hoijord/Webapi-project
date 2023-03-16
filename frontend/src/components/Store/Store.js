import React, {useEffect, useState} from 'react';
import "./Store.css"
import {url} from "../../App"
import {logout, verifyLogin} from "../Auth/AuthUtils";
import Item from "./Item/Item";
import Cart from "./Cart/Cart";

export default function Store({token, userId}) {
    const [products, setProducts] = useState([]);
    const [cart, setCartU] = useState({});
    const [filter, setFilter] = useState("");
    const [curItem, setCurItem] = useState();

    useEffect(() => {
        verifyLogin();
        getProducts();

        let cartOb = sessionStorage.getItem('cart');

        if (cartOb) {
            setCartU(JSON.parse(cartOb));
        }
    }, []);

    let setCart = function (e) {
        setCartU(e);
        sessionStorage.setItem("cart", JSON.stringify(e));
    }

    const getProducts = async () => {
        const response = await fetch(`${url}/products`);
        let data = await response.json();
        await setProducts(data);
    };
    function findProduct(productId) {
        for (let product of products) {
            if (product.productId == productId) {
                return product;
            }
        }
        return null;
    }

    async function orderCart() {
        await verifyLogin();
        await fetch(`${url}/store/purchase`, {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json",
                "token": token,
                "id": userId
            },
            body: JSON.stringify(cart)
        })

        await setCart({});
        await getProducts();
    }

    async function addToCart(id) {
        let val = {...cart};

        if (val[id]) {
            val[id]++;
        } else {
            val[id] = 1;
        }

        let prod = findProduct(id);
        val[id] = Math.min(val[id], prod.stock)

        await setCart({...cart, [id]: val[id]});
    }

    let curItemPage = null;

    if (curItem) {
        let prod = findProduct(curItem);
        curItemPage =
            <div className="item-page">
                <Item prod={prod} addToCart={addToCart} setCurItem={setCurItem}/>
            </div>
    }

    return (
        <div>
            {curItemPage}
            <div className={"App " + (curItem ? "blur" : "")}>
                <button className="logout" onClick={logout}>
                    Logout
                </button>

                <div className="cart_section">
                    <Cart cart={cart} setCart={setCart} findProduct={findProduct} orderCart={orderCart}/>
                </div>

                <div className="products_section">
                    <h1>Products</h1>
                    <input
                        className="product_search"
                        placeholder="Search..."
                        onChange={e => {
                            setFilter(e.target.value)
                        }}
                    />

                    <ul className="products">
                        {products.filter((s) =>
                            s.productName.toLowerCase().includes(filter.toLowerCase())
                            || s.productDescription.toLowerCase().includes(filter.toLowerCase())).map(prod => {
                            return (
                                <button key={prod.productId} className="product" onClick={(e) => {
                                    e.stopPropagation()
                                    setCurItem(prod.productId)
                                }
                                }>
                                    <img className="product_image" alt="Product"
                                         src={prod["productImage"] || "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Question_mark_%28black%29.svg/800px-Question_mark_%28black%29.svg.png"}/>
                                    <h4 className="product_title">{prod.productName}</h4>
                                    <p className="product_price">Price: ${prod.price}</p>
                                    <p className="product_stock">Stock: {prod.stock}</p>
                                    <input type="button" value="Add to cart" key={prod.productId} className="addToCart"
                                           onClick={(e) => {
                                               e.stopPropagation()
                                               addToCart(prod.productId)
                                           }}></input>
                                </button>
                            )
                        })} </ul>
                </div>
            </div>
        </div>
    );
}